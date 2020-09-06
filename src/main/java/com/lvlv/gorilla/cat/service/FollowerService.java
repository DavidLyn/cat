package com.lvlv.gorilla.cat.service;

import com.lvlv.gorilla.cat.mapper.FollowerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FollowerService {
    @Autowired
    FollowerMapper followerMapper;

    /**
     * 获取用户粉丝数
     * @param uid
     * @return
     */
    public int getFunCount(Long uid) {
        return followerMapper.getFanCount(uid);
    }

    /**
     * 获取用户关注数
     * @param followerId
     * @return
     */
    public int getFollowCount(Long followerId) {
        return followerMapper.getFollowCount(followerId);
    }

}
