package com.lvlv.gorilla.cat.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lvlv.gorilla.cat.annotation.UserLoginToken;
import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.service.UserService;
import com.lvlv.gorilla.cat.util.MysqlUtil;
import com.lvlv.gorilla.cat.util.PasswordUtil;
import com.lvlv.gorilla.cat.util.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    //@UserLoginToken
    //@GetMapping(value = "/test", produces = "application/json; charset=utf-8")
    //@GetMapping("/test")
    @PostMapping("/test")
    public RestResult test() {
        RestResult result = new RestResult("大小大小");
        log.debug("test");
        return result;
        //throw new RuntimeException("error");
    }

    // 手机号、密码 登录
    @PostMapping("/login")
    public RestResult login(@RequestBody User user) {

        log.debug("===============================================================");

        RestResult result = new RestResult();

        user.setUid(MysqlUtil.getNextUid());

        // 获取盐
        user.setSalt(PasswordUtil.getRandomSalt());

        // 将口令加盐并加密
        user.setPassword(PasswordUtil.getEncryptedPasswordWithSalt(user.getPassword(),user.getSalt()));

        String token = JWT.create().withAudience(user.getUid().toString())
                .sign(Algorithm.HMAC256(user.getPassword()));

        result.setMessage(token);    // 使用 message 返回 token
        result.setData(user);

        return result;
    }

    // 手机号、短信认证码登录
    @PostMapping("/smslogin")
    public RestResult smsLogin(@RequestBody User user) {

        return null;
    }

    // 手机号、密码、短信认证码注册
    @PostMapping("/register")
    public RestResult register(@RequestBody User user) {

        return null;
    }

    // 手机号、密码、短信认证码重置密码
    @PostMapping("/resetpassword")
    public RestResult resetPassword(@RequestBody User user) {

        return null;
    }

}
