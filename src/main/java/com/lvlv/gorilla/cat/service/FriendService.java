package com.lvlv.gorilla.cat.service;

import com.lvlv.gorilla.cat.mapper.FriendMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FriendService {
    @Autowired
    FriendMapper friendMapper;

    /**
     *
     * @param uid1
     * @param uid2
     */
    public void addFriend(Long uid1, Long uid2) {
        if (friendMapper.getFriendCount(uid1,uid2) == 0) {
            friendMapper.insertFriend(uid1,uid2);
        }
        if (friendMapper.getFriendCount(uid2,uid1) == 0) {
            friendMapper.insertFriend(uid2,uid1);
        }
    }
}
