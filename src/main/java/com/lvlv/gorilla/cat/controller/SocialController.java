package com.lvlv.gorilla.cat.controller;

import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.entity.sql.UserInfo;
import com.lvlv.gorilla.cat.service.FollowerService;
import com.lvlv.gorilla.cat.service.FriendService;
import com.lvlv.gorilla.cat.service.UserService;
import com.lvlv.gorilla.cat.util.RedisUtil;
import com.lvlv.gorilla.cat.util.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    private FriendService friendService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 社交 功能中获取某用户的基本信息
     *
     * @param uid
     * @return
     */
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

    /**
     * 修改 关系
     * @param map
     * @return
     */
    @PostMapping("/updateRelation")
    public RestResult updateUser(@RequestBody Map<String, String> map) {
        RestResult result = new RestResult();
        result.setData("ok");

        Long uid = Long.parseLong(map.get("uid"));
        Long friendId = Long.parseLong(map.get("friendId"));
        String relation = map.get("relation");

        try {
            friendService.updateRelation( uid, friendId, relation );
        } catch (Exception e) {
            e.printStackTrace();
            result.setData("error");
            result.setCode(-1);
            result.setMessage("DB error!");
            return result;
        }

        return result;
    }
}
