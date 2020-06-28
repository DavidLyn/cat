package com.lvlv.gorilla.cat.service;

import cn.hutool.core.date.DateUtil;
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

    /**
     * 根据 uid 查找 user
     * @param uid
     * @return
     */
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

    /**
     * 根据电话号码查找 user
     * @param mobile
     * @return
     */
    public User findUserByMobile(String mobile) {
        User user = userMapper.findUserByMobile(mobile);

        if (user != null) {
            String keyName = RedisKeyUtil.getUserKey(Long.toString(user.getUid()));
            redisUtil.set(keyName,user);
        }

        return user;
    }

    /**
     * 增加新用户
     * @param user
     */
    public void insertUser(User user) {
        userMapper.insertUser(user);
        redisUtil.set(RedisKeyUtil.getUserKey(Long.toString(user.getUid())),user);
    }

    /**
     * 更新用户
     * @param user
     */
    public void updateUser(User user) {
        userMapper.updateUser(user);
        redisUtil.set(RedisKeyUtil.getUserKey(Long.toString(user.getUid())),user);
    }

    /**
     * 检查某手机号是否已经存在
     * @param mobile
     * @return
     */
    public boolean isMobleExisted(String mobile) {
        if (userMapper.countByMobile(mobile) > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 修改昵称
     * @param uid
     * @param value
     * @return
     */
    public String updateNickname(Long uid, String value) {
        // 检查昵称已存在
        if (userMapper.countByNickname(uid,value) > 0) {
            return "昵称已存在";
        }

        try {
            userMapper.updateNickname( uid, value, DateUtil.date() );
            String keyName = RedisKeyUtil.getUserKey(Long.toString(uid));
            redisUtil.del(keyName);
            return "";
        } catch (Exception e) {
            log.error("!!!!!!!!!!!! 修改<昵称>失败:" + e.getMessage());
            return "保存失败!";
        }
    }

    /**
     * 修改 生日
     * @param uid
     * @param value
     * @return
     */
    public String updateBirthday(Long uid, String value) {
        try {
            userMapper.updateBirthday( uid, value, DateUtil.date() );
            String keyName = RedisKeyUtil.getUserKey(Long.toString(uid));
            redisUtil.del(keyName);
            return "";
        } catch (Exception e) {
            log.error("!!!!!!!!!!!! 修改<出生日期>失败:" + e.getMessage());
            return "保存失败!";
        }
    }

    /**
     * 修改 个性签名
     * @param uid
     * @param value
     * @return
     */
    public String updateProfile(Long uid, String value) {
        try {
            userMapper.updateProfile( uid, value, DateUtil.date() );
            String keyName = RedisKeyUtil.getUserKey(Long.toString(uid));
            redisUtil.del(keyName);
            return "";
        } catch (Exception e) {
            log.error("!!!!!!!!!!!! 修改<个性签名>失败:" + e.getMessage());
            return "保存失败!";
        }
    }

    /**
     * 修改 性别
     * @param uid
     * @param gender
     * @return
     */
    public String updateGender(Long uid, int gender) {
        try {
            userMapper.updateGender( uid, gender, DateUtil.date() );
            String keyName = RedisKeyUtil.getUserKey(Long.toString(uid));
            redisUtil.del(keyName);
            return "";
        } catch (Exception e) {
            log.error("!!!!!!!!!!!! 修改<性别>失败:" + e.getMessage());
            return "保存失败!";
        }
    }

}
