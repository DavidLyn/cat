package com.lvlv.gorilla.cat.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lv weiwei
 * @date 2020-05-02 5:06
 */
@Slf4j
public class MysqlUtil {

    /**
     * 获取下一个 uid
     * @return 下一个 uid
     */
    public static long getNextUid() {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);

        return snowflake.nextId();
    }

    /**
     * 获取下一个 groupId
     * @return
     */
    public static long getNextGroupId() {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);

        return snowflake.nextId();
    }

}
