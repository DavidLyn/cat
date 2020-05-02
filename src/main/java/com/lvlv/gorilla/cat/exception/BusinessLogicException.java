package com.lvlv.gorilla.cat.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务逻辑异常类
 * @author lvweiwei
 * @date 2020-05-02 01:30
 */
@Data
@AllArgsConstructor
public class BusinessLogicException extends RuntimeException {

    /**
     * 业务逻辑异常类型编码
     */
    public static Integer SERVER_ERROR = -1;
    public static Integer NOT_FIND_USER = 100;

    /**
     * 业务逻辑异常类型编码、信息映射
     */
    private final static Map<Integer,String>  map = new HashMap() {
        {
            put(BusinessLogicException.SERVER_ERROR,"服务器错误");
            put(BusinessLogicException.NOT_FIND_USER,"无效用户");
        }
    };

    /**
     * 获取 SERVER_ERROR 的错误信息
     * @return 错误信息
     */
    public static String getServerErrorMessage() {
        String message = BusinessLogicException.map.get(BusinessLogicException.SERVER_ERROR);

        if (message != null) {
            return message;
        }

        return "服务器错误";
    }

    /**
     * 根据错误编码获取错误信息
     * @return 错误信息
     */
    public String getErrorMessage() {
        String message = BusinessLogicException.map.get(this.code);

        if (message != null) {
            return message;
        }

        return "未知错误";
    }

    /**
     * 错误代码
     */
    private int code;

}
