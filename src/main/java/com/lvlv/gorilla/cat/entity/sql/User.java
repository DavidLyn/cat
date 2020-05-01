package com.lvlv.gorilla.cat.entity.sql;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long uid;
    private String name = "";
    private String nickname = "";
    private String birthday = "";
    private String mobile = "";
    private String email = "";
    private String password = "";
    private String salt = "";
    private String avatar = "";
    private Date createdAt;
    private Date updatedAt;
    private Integer status = 1;
}
