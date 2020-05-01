package com.lvlv.gorilla.cat.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author Lv weiwei
 * @date 2020-05-02 5:06
 */
public class MysqlUtil {

    /**
     * 获取下一个 uid
     * @return 下一个 uid
     */
    public static long getNextUid() {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);

        return snowflake.nextId();
    }
}
