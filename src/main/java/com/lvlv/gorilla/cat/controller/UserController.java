package com.lvlv.gorilla.cat.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lvlv.gorilla.cat.annotation.UserLoginToken;
import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.exception.BusinessLogicException;
import com.lvlv.gorilla.cat.service.UserService;
import com.lvlv.gorilla.cat.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

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

        // 创建 uid
        user.setUid(MysqlUtil.getNextUid());

        // 创建随机名字
        user.setName(IdUtil.randomUUID());

        // 创建随机盐
        user.setSalt(PasswordUtil.getRandomSalt());

        // 将口令加盐加密保存
        user.setPassword(PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(),user.getSalt()));

        user.setCreatedAt(DateUtil.date());
        user.setUpdatedAt(DateUtil.date());

        // 向 user 表中插入新记录
        try {
            userService.insertUser(user);
        } catch (Exception e) {
            log.error("insert user erorr:" + e.getMessage());

            result.setCode(-1);
            result.setMessage("insert user error!");
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

    // 手机号、密码、短信认证码 重置密码
    @PostMapping("/resetpassword")
    public RestResult resetPassword(@RequestBody User user) {

        return null;
    }

    // 向手机发短信
    @GetMapping("/sendSms")
    public RestResult sendSms(@RequestParam(value = "mobile", required = true) String mobile) {
        RestResult result = new RestResult();

        // 生成认证码
        //String vCode = RandomUtil.randomNumbers(6);
        String vCode = "123456";

        // to-do 向短信平台发送认证码

        // 将手机号和认证码保存到 redis,有效期 100 秒
        redisUtil.set(RedisKeyUtil.getMobileSmsKey(mobile),vCode,100);

        result.setData("ok");
        return result;
    }

}
