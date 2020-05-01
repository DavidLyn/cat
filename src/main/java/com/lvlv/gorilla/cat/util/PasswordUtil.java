package com.lvlv.gorilla.cat.util;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * 密码摘要相关方法
 *
 *  @author lvvv
 */
public class PasswordUtil {

    /**
     * 获取随机盐
     * @return 随机盐
     */
    public static String getRandomSalt() {
        // 返回一个32字节的随机字符串
        return RandomUtil.randomString(32);
    }

    /**
     * 返回加盐的加密密码
     * @param password 密码
     * @param salt 盐
     * @return 加盐且加密的密码
     */
    public static String getEncryptedPasswordWithSalt(String password, String salt) {

        if (StrUtil.isBlank(password) || StrUtil.isBlank(salt)) {
            throw new RuntimeException("password and salt can not be blank!");
            //return null;
        }

        // 加盐格式：salt + password + salt
        String passwordWithSalt = salt + password + salt;

        return PasswordUtil.sha256Digest(passwordWithSalt);
    }

    /**
     * 返回 md5 摘要
     * @param data 原始数据
     * @return md5 摘要
     */
    public static String md5Digest(String data) {
        return DigestUtil.md5Hex(data);
    }

    /**
     * 返回 sha256 摘要
     * @param data 原始数据
     * @return sha256 摘要
     */
    public static String sha256Digest(String data) {
        byte[] sha256 = DigestUtil.sha256(data);
        return BCD.bcdToStr(sha256);
    }

}
