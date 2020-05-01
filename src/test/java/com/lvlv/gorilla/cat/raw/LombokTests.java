package com.lvlv.gorilla.cat.raw;

import com.lvlv.gorilla.cat.entity.User;
import org.junit.jupiter.api.Test;

public class LombokTests {
    @Test
    void test() {
        User user = new User();

        user.setName("kaku");
    }
}
