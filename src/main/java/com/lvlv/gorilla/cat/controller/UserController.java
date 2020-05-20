package com.lvlv.gorilla.cat.controller;

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

        return null;
    }

    // 手机号、密码、短信认证码注册
    @PostMapping("/register")
    public RestResult register(@RequestBody User user) {
        RestResult result = new RestResult();

        user.setUid(MysqlUtil.getNextUid());

        // 获取盐
        user.setSalt(PasswordUtil.getRandomSalt());

        // 将口令加盐并加密
        user.setPassword(PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(),user.getSalt()));

        String token = JWT.create().withAudience(user.getUid().toString())
                .sign(Algorithm.HMAC256(user.getPassword()));

        // 将 token 存入 redis

        result.setMessage(token);    // 使用 message 返回 token
        result.setData(user);

        return result;
    }

    // 手机号、密码、短信认证码重置密码
    @PostMapping("/resetpassword")
    public RestResult resetPassword(@RequestBody User user) {

        return null;
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

        result.setData("ok");
        return result;
    }

}
