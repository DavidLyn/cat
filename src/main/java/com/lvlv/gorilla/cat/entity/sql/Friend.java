package com.lvlv.gorilla.cat.entity.sql;

import lombok.Data;
import java.util.Date;

@Data
public class Friend {
    private int id;               // 主键

    private Long uid;             // 当前用户 uid
    private Long friendId;        // 好友 uid
    private String relation;      // 关系
    private int state;            // 状态 1-正常 0-删除/拉黑
    private Date friendTime;      // 好友时间
    private Date updateTime;      // 修改时间
    private Date deleteTime;      // 删除/拉黑时间
}
