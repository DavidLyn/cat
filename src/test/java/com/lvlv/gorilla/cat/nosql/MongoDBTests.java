package com.lvlv.gorilla.cat.nosql;

import com.lvlv.gorilla.cat.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class MongoDBTests {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void test() {
        Criteria criteria = new Criteria();

        criteria.and("name").is("lvvv");

        Query query = new Query(criteria);

        // 分页,排序
        query.skip(1).limit(10).with(Sort.by(Sort.Order.desc("name")));

        Long cc = mongoTemplate.count(query,"user");

        System.out.println("++++++++++++++++++++++++++ count = " + cc);

        List<User> list = mongoTemplate.find(query, User.class);

        for (User user: list
             ) {
            System.out.println("______________________________ user = " + user.getName() + user.getAge());
        }

    }
}
