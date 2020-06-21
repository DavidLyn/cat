package com.lvlv.gorilla.cat.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvlv.gorilla.cat.entity.Address;
import com.lvlv.gorilla.cat.entity.User;
import org.junit.jupiter.api.Test;

// https://blog.csdn.net/phone13144830339/article/details/80078914
public class JsonTest {
    @Test
    public void test() {
        ObjectMapper mapper = new ObjectMapper();

        User user = new User();
        user.setName("asd");
        //user.setAge(20);
        //user.setEdu("fgdfs");
        Address address = new Address();
        address.setCity("tianjin");
        user.getList().add(address);

        String ss = "";
        try {
            ss = mapper.writeValueAsString(user);
            System.out.println("obj2json = " + ss);
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }

        User user1;
        try {
            user1 = mapper.readValue(ss, User.class);
            System.out.println("json2obj name = " + user1.getName());
            System.out.println("json2obj sex = " + user1.getSex());
            System.out.println("json2obj edu = " + user1.getEdu());
            System.out.println("json2obj list = " + user1.getList());
        } catch (Exception e) {

        }
    }

}
