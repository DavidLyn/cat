package com.lvlv.gorilla.cat.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@Document("user")
public class User {
    private String name;
    private int age;
    private int sex;
    private String edu;
    private List<Address> list = new LinkedList<>();

}
