package com.lvlv.gorilla.cat.controller;

import com.lvlv.gorilla.cat.annotation.UserLoginToken;
import com.lvlv.gorilla.cat.exception.BusinessLogicException;
import com.lvlv.gorilla.cat.util.RestResult;
import com.lvlv.gorilla.cat.util.RestStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.prefs.BackingStoreException;

@RestController
@RequestMapping("/user")
public class UserController {

    @UserLoginToken
    @GetMapping(value = "/test", produces = "application/json; charset=utf-8")
    public RestResult test() {
        RestResult result = new RestResult("大小大小");
        return result;
        //throw new BusinessLogicException(RestStatus.AUTHENTICATION_INVALID_USER);
    }
}
