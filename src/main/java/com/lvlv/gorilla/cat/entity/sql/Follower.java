package com.lvlv.gorilla.cat.entity.sql;

import lombok.Data;

import java.util.Date;

@Data
public class Follower {
    private int id;               // 主键

    private Long uid;             // 当前用户 uid
    private Long followerId;      // 追随者 uid
    private int state;            // 状态 1-正常 0-删除
    private Date followTime;      // 追随时间
    private Date deleteTime;      // 删除/拉黑时间
}
