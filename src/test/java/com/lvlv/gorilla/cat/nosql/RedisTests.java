package com.lvlv.gorilla.cat.nosql;

import com.lvlv.gorilla.cat.entity.User;
import com.lvlv.gorilla.cat.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    void test() {
        //System.out.println("======================== hello : " + redisUtil.get("hello"));
        redisUtil.set("k1","hello world!");
        System.out.println("======================== k1 : " + redisUtil.get("k1"));

        User user = new User();

        user.setName("lvvv");

        redisUtil.set("user1",user);

        User user1 = (User) redisUtil.get("user1");

        System.out.println("======================== user name : " + user1.getName());

    }
}
