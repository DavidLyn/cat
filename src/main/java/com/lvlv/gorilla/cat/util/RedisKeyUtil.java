package com.lvlv.gorilla.cat.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lv weiwei
 * @date 2020/05/02 3:55
 */
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
}
