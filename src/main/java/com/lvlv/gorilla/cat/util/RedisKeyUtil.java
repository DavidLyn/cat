package com.lvlv.gorilla.cat.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisKeyUtil {

    /**
     * 获取 user 在 Redis 中的 key name
     * @param uid
     * @return
     */
    public static String getUserKey(String uid) {
        return "user:" + uid;
    }

    /**
     * 获取 发往某手机号的短信 的 key name
     * @param mobile
     * @return
     */
    public static String getMobileSmsKey(String mobile) {
        return "sms:" + mobile;
    }

    /**
     * 获取某 userid token 的 key name
     * @param uid
     * @return
     */
    public static String getTokenKey(String uid) {
        return "token:" + uid;
    }

    /**
     * 获取某 uid 是否在线 的 key name
     * @param uid
     * @return
     */
    public static String getMQTTOnlineKey(String uid) {
        return "mqtt:online:" + uid;
    }
}
