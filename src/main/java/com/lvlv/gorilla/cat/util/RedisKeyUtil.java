package com.lvlv.gorilla.cat.util;

/**
 * @author lv weiwei
 * @date 2020/05/02 3:55
 */
public class RedisKeyUtil {

    /**
     * 获取 user 在 Redis 中的 key name
     * @param uid
     * @return
     */
    public static String getUserKey(String uid) {
        return "user:" + uid;
    }
}
