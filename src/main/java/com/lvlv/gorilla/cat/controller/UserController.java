package com.lvlv.gorilla.cat.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lvlv.gorilla.cat.annotation.UserLoginToken;
import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.exception.BusinessLogicException;
import com.lvlv.gorilla.cat.service.UserService;
import com.lvlv.gorilla.cat.service.tool.AliOssService;
import com.lvlv.gorilla.cat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AliOssService aliOssService;

    @Autowired
    private RedisUtil redisUtil;

    //@UserLoginToken
    //@GetMapping(value = "/test", produces = "application/json; charset=utf-8")
    //@GetMapping("/test")
    @PostMapping("/test")
    public RestResult test() {
        RestResult result = new RestResult("大小大小");
        return result;
        //throw new RuntimeException("error");
    }

    // 手机号、密码 登录
    @PostMapping("/login")
    public RestResult login(@RequestBody User user) {
        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
             StrUtil.isBlank(user.getPassword()) ||
             user.getUid() == null ||
             user.getUid() == 0 ) {
            result.setCode(-1);
            result.setMessage("invalid parameters");
            return result;
        }

        User rUser = userService.findUserByUid(user.getUid());
        if (rUser == null) {
            result.setCode(-1);
            result.setMessage("not found user");
            return result;
        }

        // 根据原有盐值计算输入密码的加密值
        String inputEncryptedPassword = PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(), rUser.getSalt());

        if (inputEncryptedPassword.equals(rUser.getPassword())) {
            result.setData(rUser);
            return result;   // 登录成功
        } else {
            result.setCode(-1);
            result.setMessage("invalid password");
            return result;
        }
    }

    // 手机号、短信认证码登录
    @PostMapping("/smslogin")
    public RestResult smsLogin(@RequestBody User user) {
        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
                StrUtil.isBlank(user.getSalt()) ||
                user.getUid() == null ||
                user.getUid() == 0 ) {
            result.setCode(-1);
            result.setMessage("invalid parameters");
            return result;
        }

        // 短信认证码使用 salt 传递
        String vCode = user.getSalt();

        // 检查验证码
        if (redisUtil.hasKey(RedisKeyUtil.getMobileSmsKey(user.getMobile()))) {
            String smsCode = (String) redisUtil.get(RedisKeyUtil.getMobileSmsKey(user.getMobile()));
            if (!vCode.equals(smsCode)) {
                result.setCode(-1);
                result.setMessage("verified code error");
                return result;
            }
        } else {
            result.setCode(-1);
            result.setMessage("no sms in cache");
            return result;
        }

        User rUser = userService.findUserByUid(user.getUid());
        if (rUser == null) {
            result.setCode(-1);
            result.setMessage("not found user");
            return result;
        }

        result.setData(rUser);
        return result;   // 登录成功
    }

    // 手机号、密码、短信认证码注册
    @PostMapping("/register")
    public RestResult register(@RequestBody User user) {
        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
             StrUtil.isBlank(user.getPassword()) ||
             StrUtil.isBlank(user.getSalt()) ) {

            result.setCode(-1);
            result.setMessage("invalid parameters");
            return result;
        }

        // 短信认证码使用 salt 传递
        String vCode = user.getSalt();

        // 检查验证码
        if (redisUtil.hasKey(RedisKeyUtil.getMobileSmsKey(user.getMobile()))) {
            String smsCode = (String) redisUtil.get(RedisKeyUtil.getMobileSmsKey(user.getMobile()));
            if (!vCode.equals(smsCode)) {
                result.setCode(-1);
                result.setMessage("verified code error");
                return result;
            }
        } else {
            result.setCode(-1);
            result.setMessage("no sms in cache");
            return result;
        }

        // 检查手机号是否已经注册
        if (userService.isMobleExisted(user.getMobile())) {
            result.setCode(-1);
            result.setMessage("mobile already registered");
            return result;
        }

        // 创建新 user 对象
        User newUser = new User();

        // 参数中唯一可直接使用的是电话号码
        newUser.setMobile(user.getMobile());

        // 创建 uid
        newUser.setUid(MysqlUtil.getNextUid());

        // 创建随机名字
        newUser.setName(IdUtil.randomUUID());

        // 创建随机盐
        newUser.setSalt(PasswordUtil.getRandomSalt());

        // 将口令加盐加密保存
        newUser.setPassword(PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(),newUser.getSalt()));

        newUser.setCreatedAt(DateUtil.date());
        newUser.setUpdatedAt(DateUtil.date());

        // 向 user 表中插入新记录
        try {
            userService.insertUser(newUser);
        } catch (Exception e) {
            log.error("insert user erorr:" + e.getMessage());

            result.setCode(-1);
            result.setMessage("insert user error!");
            return result;
        }

        // 创建 token
        String token = JWT.create().withAudience(newUser.getUid().toString())
                .sign(Algorithm.HMAC256(newUser.getPassword()));

        // 将 token 存入 redis
        redisUtil.set(RedisKeyUtil.getTokenKey(newUser.getUid().toString()),token);

        result.setMessage(token);    // 使用 message 返回 token
        result.setData(newUser);

        return result;
    }

    // 手机号、密码、短信认证码 重置密码
    @PostMapping("/resetpassword")
    public RestResult resetPassword(@RequestBody User user) {
        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
             StrUtil.isBlank(user.getPassword()) ||
             StrUtil.isBlank(user.getSalt()) ||
             user.getUid() == null ||
             user.getUid() == 0 ) {

            result.setCode(-1);
            result.setMessage("invalid parameters");
            return result;
        }

        // 短信认证码使用 salt 传递
        String vCode = user.getSalt();

        // 检查验证码
        if (redisUtil.hasKey(RedisKeyUtil.getMobileSmsKey(user.getMobile()))) {
            String smsCode = (String) redisUtil.get(RedisKeyUtil.getMobileSmsKey(user.getMobile()));
            if (!vCode.equals(smsCode)) {
                result.setCode(-1);
                result.setMessage("verified code error");
                return result;
            }
        } else {
            result.setCode(-1);
            result.setMessage("no sms in cache");
            return result;
        }

        // 查询当前用户
        User rUser = userService.findUserByUid(user.getUid());
        if (rUser == null) {
            result.setCode(-1);
            result.setMessage("not found user");
            return result;
        }

        // 重新创建随机盐
        user.setSalt(PasswordUtil.getRandomSalt());

        // 将新口令加盐加密保存
        user.setPassword(PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(),user.getSalt()));

        // 设置更新时间
        user.setUpdatedAt(DateUtil.date());

        // 更新数据库 user 记录
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            log.error("update user erorr:" + e.getMessage());

            result.setCode(-1);
            result.setMessage("update user error!");
            return result;
        }

        // 创建 token
        String token = JWT.create().withAudience(user.getUid().toString())
                .sign(Algorithm.HMAC256(user.getPassword()));

        // 将 token 存入 redis
        redisUtil.set(RedisKeyUtil.getTokenKey(user.getUid().toString()),token);

        result.setMessage(token);    // 使用 message 返回 token
        result.setData(user);

        return result;
    }

    // 向手机发短信
    @GetMapping("/sendSms")
    public RestResult sendSms(@RequestParam(value = "mobile", required = true) String mobile) {
        RestResult result = new RestResult();

        // 生成认证码
        String vCode = RandomUtil.randomNumbers(6);

        // to-do 向短信平台发送认证码

        // 将手机号和认证码保存到 redis,有效期 100 秒
        redisUtil.set(RedisKeyUtil.getMobileSmsKey(mobile),vCode,100);

        // 为测试方便将验证码返回 app 端
        result.setData(vCode);

        return result;
    }

    // 用户退出登录???
    @GetMapping("/logout")
    public RestResult logout(@RequestParam(value = "uid", required = true) Long uid) {
        RestResult result = new RestResult();

        return result;
    }

    // 上传并修改用户头像
    @PostMapping("/uploadAvatar")
    public RestResult uploadAvatar( @RequestParam(value = "uid", required = true) Long uid,
                                    @RequestParam("file") MultipartFile file ) {
        RestResult result = new RestResult();

        // 检查参数
        if ( uid == null || uid == 0 ) {
            result.setCode(-1);
            result.setMessage("invalid uid");

            return result;
        }

        // 查找 user
        User user = userService.findUserByUid(uid);

        if (user == null) {
            result.setCode(-1);
            result.setMessage("user not existed");

            return result;
        }

        // 记录原有头像 url
        String oldAvatarFileUrl = user.getAvatar();

        // 获取上载文件原始名称
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isEmpty(originalFilename)) {
            result.setCode(-1);
            result.setMessage("invalid upload file name");
            return result;
        }

        // 获取原始文件后缀并生成新文件名
        String type = "";

        if (originalFilename.lastIndexOf(".") >= 0) {
            type = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        String newFileName = IdUtil.simpleUUID();

        if (StrUtil.isNotEmpty(type)) {
            newFileName = newFileName + "." + type;
        }

        // 上传文件 -- 测试时暂上传至本地
        //String newAvatarFileUrl = aliOssService.uploadToOSS( newFileName, file );
        String newAvatarFileUrl = aliOssService.uploadToLocal( newFileName, file );

        if (StrUtil.isEmpty(newAvatarFileUrl)) {
            result.setCode(-1);
            result.setMessage("upload to ass error");
            return result;
        }

        // 修改 user 对象
        user.setAvatar(newAvatarFileUrl);

        try {
            userService.updateUser(user);
        } catch (Exception e) {
            aliOssService.deleteFromOSS(newAvatarFileUrl);

            result.setCode(-1);
            result.setMessage("update user error");
            return result;
        }

        // 删除原有头像文件 -- 测试时暂注释掉
//        if (StrUtil.isNotEmpty(oldAvatarFileUrl)) {
//            aliOssService.deleteFromOSS(oldAvatarFileUrl);
//        }

        return result;
    }
}
