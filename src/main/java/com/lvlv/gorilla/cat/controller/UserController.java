package com.lvlv.gorilla.cat.controller;

import com.lvlv.gorilla.cat.annotation.UserLoginToken;
import com.lvlv.gorilla.cat.util.RestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @UserLoginToken
    //@GetMapping(value = "/test", produces = "application/json; charset=utf-8")
    @GetMapping("/test")
    public RestResult test() {
        RestResult result = new RestResult("大小大小");
        return result;
        //throw new RuntimeException("error");
    }
}
