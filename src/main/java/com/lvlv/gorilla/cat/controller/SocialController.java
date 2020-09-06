package com.lvlv.gorilla.cat.controller;

import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.entity.sql.UserInfo;
import com.lvlv.gorilla.cat.service.FollowerService;
import com.lvlv.gorilla.cat.service.UserService;
import com.lvlv.gorilla.cat.util.RedisUtil;
import com.lvlv.gorilla.cat.util.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供与 社交 相关的服务
 */
@RestController
@RequestMapping("/social")
@Slf4j
public class SocialController {
    @Autowired
    private UserService userService;

    @Autowired
    private FollowerService followerService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/userInfo")
    public RestResult getUserInfo(@RequestParam(value = "uid", required = true) Long uid) {
        RestResult result = new RestResult();

        User user = userService.findUserByUid(uid);
        if (user == null) {
            result.setCode(-1);
            result.setMessage("not found user");
            return result;
        }

        UserInfo userInfo = new UserInfo();

        userInfo.setUid(user.getUid());
        userInfo.setName(user.getName());
        userInfo.setNickname(user.getNickname());
        userInfo.setBirthday(user.getBirthday());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setCreatedAt(user.getCreatedAt());
        userInfo.setGender(user.getGender());
        userInfo.setProfile(user.getProfile());

        userInfo.setFanNumber(followerService.getFunCount(uid));
        userInfo.setFollowNumber(followerService.getFollowCount(uid));

        result.setData(userInfo);

        return result;
    }

}
