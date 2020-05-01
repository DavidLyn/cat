package com.lvlv.gorilla.cat.util;

import java.util.concurrent.TimeUnit;

/**
 * 状态枚举,为 RedisUtil 所用
 * @program: dgpoms-server-root
 * @Date: 2018/12/27 11:40
 * @Author: Mr.Zheng
 * @Description: from Internet
 */
public abstract  class Status {

    /**
     * 过期时间相关枚举
     */
    public static enum ExpireEnum{
        //未读消息的有效期为30天
        UNREAD_MSG(30L, TimeUnit.DAYS)
        ;

        /**
         * 过期时间
         */
        private Long time;
        /**
         * 时间单位
         */
        private TimeUnit timeUnit;

        ExpireEnum(Long time, TimeUnit timeUnit) {
            this.time = time;
            this.timeUnit = timeUnit;
        }

        public Long getTime() {
            return time;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }
    }
}