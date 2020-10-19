package com.lvlv.gorilla.cat.entity.sql;

import lombok.Data;

import java.util.Date;

@Data
public class GroupMain {
    private Long groupId;            // 群组ID
    private String tag;              // 标签
    private String name;             // 群组名
    private String avatar;           // 头像
    private String profile;          // 群组简介
    private Date createTime;         // 创建时间
    private Date updateTime;         // 修改时间
    private Date dismissTime;        // 解散时间
    private int state;               // 状态 1-正常 0-解散
}
