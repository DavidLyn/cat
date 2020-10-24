package com.lvlv.gorilla.cat.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvlv.gorilla.cat.annotation.UserLoginToken;
import com.lvlv.gorilla.cat.config.AliSmsConfig;
import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.exception.BusinessLogicException;
import com.lvlv.gorilla.cat.service.UserService;
import com.lvlv.gorilla.cat.service.tool.AliOssService;
import com.lvlv.gorilla.cat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AliOssService aliOssService;

    @Autowired
    AliSmsConfig aliSmsConfig;

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

    /**
     * 手机号、密码 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public RestResult login(@RequestBody User user) {
        log.info("-----------------------> login 被调用!");

        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
             StrUtil.isBlank(user.getPassword()) ) {
            result.setCode(-1);
            result.setMessage("invalid parameters");
            log.error("-----------------------> login error : invalid parameters");
            return result;
        }

        User rUser;
        try {
            if (user.getUid() == null || user.getUid() == 0) {
                rUser = userService.findUserByMobile(user.getMobile());
            } else {
                rUser = userService.findUserByUid(user.getUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
            rUser = null;
        }

        if (rUser == null) {
            result.setCode(-1);
            result.setMessage("not found user");
            log.error("-----------------------> login error : not found user");
            return result;
        }

        // 根据原有盐值计算输入密码的加密值
        String inputEncryptedPassword = PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(), rUser.getSalt());

        if (!inputEncryptedPassword.equals(rUser.getPassword())) {
            result.setCode(-1);
            result.setMessage("invalid password");
            log.error("-----------------------> login error : invalid password");
            return result;
        }

        setTokenAndResetSalt(rUser);

        result.setData(rUser);

        // 保存用户登录状态
        userService.login(rUser.getUid());

        return result;
    }

    /**
     * 手机号、短信认证码登录
     * @param user
     * @return
     */
    @PostMapping("/smslogin")
    public RestResult smsLogin(@RequestBody User user) {
        log.info("-----------------------> smslogin 被调用!");
        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
             StrUtil.isBlank(user.getSalt())  ) {
            result.setCode(-1);
            result.setMessage("invalid parameters");
            log.error("-----------------------> smslogin error : invalid parameters");
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
                log.error("-----------------------> smslogin error : verified code error");
                return result;
            }
        } else {
            result.setCode(-1);
            result.setMessage("no sms in cache");
            return result;
        }

        User rUser;
        try {
            if (user.getUid() == null || user.getUid() == 0 ) {
                rUser = userService.findUserByMobile(user.getMobile());
            } else {
                rUser = userService.findUserByUid(user.getUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
            rUser = null;
        }

        if (rUser == null) {
            result.setCode(-1);
            result.setMessage("not found user");
            log.error("-----------------------> smslogin error : not found user");
            return result;
        }

        setTokenAndResetSalt(rUser);

        result.setData(rUser);

        // 保存用户登录状态
        userService.login(rUser.getUid());

        return result;
    }

    /**
     * 手机号、密码、短信认证码注册
     * @param user
     * @return
     */
    @PostMapping("/register")
    public RestResult register(@RequestBody User user) {
        log.info("-----------------------> register 被调用!");

        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
             StrUtil.isBlank(user.getPassword()) ||
             StrUtil.isBlank(user.getSalt()) ) {

            result.setCode(-1);
            result.setMessage("invalid parameters");
            log.error("-----------------------> register error : invalid parameters");
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
                log.error("-----------------------> register error : verified code error");
                return result;
            }
        } else {
            result.setCode(-1);
            result.setMessage("no sms in cache");
            log.error("-----------------------> register error : no sms in cache");
            return result;
        }

        // 检查手机号是否已经注册
        if (userService.isMobleExisted(user.getMobile())) {
            result.setCode(-1);
            result.setMessage("mobile already registered");
            log.error("-----------------------> register error : mobile already registered");
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

        setTokenAndResetSalt(newUser);

        result.setData(newUser);

        return result;
    }

    /**
     * 手机号、密码、短信认证码 重置密码
     * @param user
     * @return
     */
    @PostMapping("/resetpassword")
    public RestResult resetPassword(@RequestBody User user) {
        RestResult result = new RestResult();

        if ( StrUtil.isBlank(user.getMobile())   ||
             StrUtil.isBlank(user.getPassword()) ||
             StrUtil.isBlank(user.getSalt()) ||
             user.getUid() == null ||
             user.getUid() == 0 ) {

            result.setCode(-1);
            result.setMessage("Invalid parameters");
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
        rUser.setSalt(PasswordUtil.getRandomSalt());

        // 将新口令加盐加密保存
        rUser.setPassword(PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(),rUser.getSalt()));

        // 设置更新时间
        rUser.setUpdatedAt(DateUtil.date());

        // 更新数据库 user 记录
        try {
            userService.updateUser(rUser);
        } catch (Exception e) {
            log.error("update user erorr:" + e.getMessage());

            result.setCode(-1);
            result.setMessage("update user error!");
            return result;
        }

        setTokenAndResetSalt(rUser);

        result.setData(rUser);

        return result;
    }

    /**
     * 向手机发短信
     * @param mobile
     * @return
     */
    @GetMapping("/sendSms")
    public RestResult sendSms(@RequestParam(value = "mobile", required = true) String mobile) {
        log.info("-----------------------> sendSms 被调用!");

        RestResult result = new RestResult();

        // 生成认证码
        String vCode = RandomUtil.randomNumbers(6);

        // 向短信平台发送认证码
        if (!aliSmsConfig.sendSms(mobile,vCode)) {
            result.setCode(-1);
            result.setMessage("sendSms error");
            return result;
        }

        // 将手机号和认证码保存到 redis,考虑到复杂的网络环境,有效期设置 600 秒(10分钟)
        redisUtil.set(RedisKeyUtil.getMobileSmsKey(mobile),vCode,600);

        // 为测试方便将验证码返回 app 端
        result.setData(vCode);

        return result;
    }

    // 用户退出登录
    @GetMapping("/logout")
    public RestResult logout(@RequestParam(value = "uid", required = true) Long uid) {
        log.info("-----------------------> logout 被调用!");
        RestResult result = new RestResult();

        // 修改数据库用户记录状态 删除 Redis 相关信息
        userService.logout(uid);

        return result;
    }

    /**
     * 修改用户信息
     * @param map
     * @return
     */
    @PostMapping("/update")
    public RestResult updateUser( @RequestBody Map<String,String> map ) {
        RestResult result = new RestResult();

        Long uid = Long.parseLong(map.get("uid"));
        String fieldName = map.get("fieldName");
        String value = map.get("value");

        switch (fieldName) {
            case "nickname" : {
                String ret = userService.updateNickname(uid,value);
                if (!StrUtil.isBlank(ret)) {
                    result.setCode(-1);
                    result.setMessage(ret);
                }
                break;
            }
            case "birthday" : {
                String ret = userService.updateBirthday(uid,value);
                if (!StrUtil.isBlank(ret)) {
                    result.setCode(-1);
                    result.setMessage(ret);
                }
                break;
            }
            case "profile" : {
                String ret = userService.updateProfile(uid,value);
                if (!StrUtil.isBlank(ret)) {
                    result.setCode(-1);
                    result.setMessage(ret);
                }
                break;
            }
            case "gender" : {
                int gender = Integer.parseInt(value);
                String ret = userService.updateGender(uid,gender);
                if (!StrUtil.isBlank(ret)) {
                    result.setCode(-1);
                    result.setMessage(ret);
                }
                break;
            }
            default:{
                result.setCode(-1);
                result.setMessage("no defined!");
            }
        }

        return result;
    }

    /**
     * 上传并修改用户头像
     * @param uid
     * @param file
     * @return
     */
    @PostMapping("/uploadAvatar")
    public RestResult uploadAvatar( @RequestParam(value = "uid", required = true) Long uid,
                                    @RequestParam("file") MultipartFile file ) {
        RestResult result = new RestResult();

        // 检查参数
        if ( uid == null || uid == 0 ) {
            result.setCode(-1);
            result.setMessage("Invalid uid");

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
            result.setMessage("Invalid upload file name");
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

        // 上传文件至 Aliyun oss
        String newAvatarFileUrl = aliOssService.uploadToOSS( newFileName, file );
        // 上传文件 -- 测试时暂上传至本地
        //String newAvatarFileUrl = aliOssService.uploadToLocal( newFileName, file );

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
            log.error("Update User error : " + e.getMessage());

            // 注意此处应用 newFileName 而不是 newAvatarFileUrl
            aliOssService.deleteFromOSS(newFileName);

            result.setCode(-1);
            result.setMessage("update user error");
            return result;
        }

        // 删除原有头像文件,注意:不能用 url,需要用 文件名
        String oldFileName = StrUtil.subAfter(oldAvatarFileUrl,"/",true);
        if (StrUtil.isNotEmpty(oldFileName)) {
            aliOssService.deleteFromOSS(oldFileName);
        }

        // 返回 头像 url
        result.setData(newAvatarFileUrl);

        return result;
    }

    /**
     * 根据 手机号 查询用户信息
     * @param mobile
     * @return
     */
    @GetMapping("/searchByMobileOrName")
    public RestResult searchByMobileOrName(@RequestParam(value = "mobile", required = true) String mobile) {
        RestResult result = new RestResult();

        List<User> list;
        User user;
        try {
            list = userService.searchByMobile(mobile);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(-1);
            result.setMessage("DB error!");
            return result;
        }

        log.info("--------------> list" + list.toString());

        ObjectMapper mapper = new ObjectMapper();
        String listFormatJson;
        try {
            listFormatJson = mapper.writeValueAsString(list);
        } catch (Exception e) {
            listFormatJson = "";
        }

        result.setData(listFormatJson);
        return result;
    }

    /**
     * 重载朋友信息
     * @param uid
     * @return
     */
    @GetMapping("/reloadFriends")
    public RestResult reloadFriends(@RequestParam(value = "uid", required = true) Long uid) {
        RestResult result = new RestResult();

        try {
            List<Map<String, Object>> friends = userService.getFriendsList(uid);
            log.debug("-----------------------------> reloadFriends = " + friends.toString() );
            result.setData(friends);
        } catch (Exception e) {
            log.error("reloadFriends error : " + e.getMessage());
            result.setMessage("DB error!");
            result.setCode(-1);
        }

        return result;
    }

     /**
      * 为向前端返回的 user 设置 token,并复位 salt
      * @param user
      */
    private void setTokenAndResetSalt(User user) {
        // 创建 token
        String token = JWT.create().withAudience(user.getUid().toString())
                .sign(Algorithm.HMAC256(user.getPassword()));

        // 将 token 存入 redis
        redisUtil.set(RedisKeyUtil.getTokenKey(user.getUid().toString()),token);

        // 使用 password 返回 token
        user.setPassword(token);

        // reset salt
        user.setSalt(null);
    }
}
