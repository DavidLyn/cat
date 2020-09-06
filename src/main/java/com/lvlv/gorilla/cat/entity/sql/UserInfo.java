package com.lvlv.gorilla.cat.entity.sql;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {
    private Long uid;
    private String name = "";
    private String nickname = "";
    private Integer gender = 0;
    private String birthday = "";
    private String avatar = "";
    private String profile = "";
    private Date createdAt;

    private int fanNumber;       // 粉丝数量
    private int followNumber;    // 关注数量

}
