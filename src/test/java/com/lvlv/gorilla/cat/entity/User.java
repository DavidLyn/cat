package com.lvlv.gorilla.cat.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document("user")
public class User {
    private String name;
    private int age;
    private int sex;
    private String edu;
}
