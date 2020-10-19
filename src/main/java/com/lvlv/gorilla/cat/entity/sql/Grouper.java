package com.lvlv.gorilla.cat.entity.sql;

import lombok.Data;

import java.util.Date;

@Data
public class Grouper {
    private int id;               // 主键

    private Long groupId;         // 群组ID
    private Long uid;             // 用户 uid
    private int role;             // 角色 0-群主 1-管理员 9-普通组员
    private Date joinTime;        // 入群时间
    private Date updateTime;      // 修改时间
    private Date quitTime;        // 退群时间
    private int state;            // 状态 1-正常 0-已退群
}
