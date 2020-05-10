package com.lvlv.gorilla.cat.service;

import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.mapper.UserMapper;
import com.lvlv.gorilla.cat.util.RedisKeyUtil;
import com.lvlv.gorilla.cat.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    public User findUserByUid(long uid) {

        String keyName = RedisKeyUtil.getUserKey(Long.toString(uid));

        if (redisUtil.hasKey(keyName)) {
            return (User) redisUtil.get(keyName);
        }

        User user = userMapper.findUserByUid(uid);

        if (user != null) {
            redisUtil.set(keyName,user);
        }

        return user;
    }
}
