package com.lvlv.gorilla.cat.raw;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.junit.jupiter.api.Test;

public class HutoolTests {
    @Test
    void test() {
        String uuid = IdUtil.randomUUID();
        System.out.println("++++++++++++++++++++++++++ uuid = " + uuid);

        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        // long id = snowflake.nextId();
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());
        System.out.println("++++++++++++++++++++++++++ Snowflake id = " + snowflake.nextId());

        System.out.println("++++++++++++++++++++++++++++++++ now = " + DateTime.now().toString());
    }

    @Test
    void testHash() {
        String md5Str = DigestUtil.md5Hex("Passw0rdrwerkjsdfjkgfnmnmbbjkjgfvcbbfsdffhsdkfsdfmnbvjksdfhqlwyirohkfv,has;dn,chfhflfkdfdhfjmvfjlgiuoreklwr392r7dhfsdkfhsdfdskfefiuefdlfjklsdfj;fjlakfjd;fjfoepwurdlfkdskfsfjhlsfkhdsfkldhsfsdhf");

        byte[] sha256 = DigestUtil.sha256("Passw0rdbdbdfueiweuihfsdkfhdsfksdfuowerufdsjkfdsjfieurfweirdhfdfdsfdsjfefj.f,ew;lfjfoief,fn,mzxcbkldhwdlwqueojdflkdsjfdlfefojklfdhflsfdifuewrlkjfkdfjeirewklfjdskfhifewri");

        String sha256Str = BCD.bcdToStr(sha256);

        System.out.println("++++++++++++++++++++++++++++++++++++ md5 = " + md5Str + "    length = " + md5Str.length());

        System.out.println("++++++++++++++++++++++++++++++++++++ sha256 = " + sha256Str + "    length = " + sha256Str.length());

        String randomStr = RandomUtil.randomString(32);
        System.out.println("++++++++++++++++++++++++++++++++++++ randomStr = " + randomStr);
        System.out.println("++++++++++++++++++++++++++++++++++++ randomNumbers = " + RandomUtil.randomNumbers(32));

    }

    @Test
    void strTest() {
        if (StrUtil.isEmpty("     ")) {
            System.out.println("8888888");
        } else {
            System.out.println("0000000");
        }
        if (StrUtil.isEmpty("")) {
            System.out.println("9999999");
        } else {
            System.out.println("0000000");
        }

        if (StrUtil.isEmpty(null)) {
            System.out.println("9999999");
        } else {
            System.out.println("0000000");
        }

        if (StrUtil.isBlank("     ")) {
            System.out.println("8888888");
        } else {
            System.out.println("0000000");
        }

        if (StrUtil.isBlank("")) {
            System.out.println("9999999");
        } else {
            System.out.println("0000000");
        }

        if (StrUtil.isBlank(null)) {
            System.out.println("9999999");
        } else {
            System.out.println("0000000");
        }

    }
}
