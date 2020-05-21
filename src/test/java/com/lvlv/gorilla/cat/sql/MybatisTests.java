package com.lvlv.gorilla.cat.sql;

import cn.hutool.core.date.DateUtil;
import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.mapper.UserMapper;
import com.lvlv.gorilla.cat.util.MysqlUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Date;

@SpringBootTest
public class MybatisTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsertUser() {
        //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$ testSqlConnent = " + userMapper.testSqlConnent());

        User user = new User();
        user.setUid(MysqlUtil.getNextUid());
        user.setName("Lv weiwei6");
        user.setCreatedAt(DateUtil.date());
        user.setUpdatedAt(DateUtil.date());
        //user.setCreatedAt(new Date());
        user.setMobile("13301133157");

        try {
            userMapper.insertUser(user);
        } catch (Exception e) {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$ Exception : " + e.getMessage());
        }

        //HttpStatus.ACCEPTED
    }

    @Test
    void testFindUser() {
        User user = userMapper.findUserByUid(6);
        if (user != null) {
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& created time = " + DateUtil.formatDateTime(user.getCreatedAt()));
        }
    }

    @Test
    void testCountAll() {
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& count all = " + userMapper.conntAll());
    }

    @Test
    void testCountMobile() {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ mobile count = " + userMapper.countByMobile("13301133157"));
    }
}
