package com.lvlv.gorilla.cat.exception;

import com.lvlv.gorilla.cat.util.RestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 业务逻辑异常类
 * @author lvweiwei
 * @date 2020-05-02 01:30
 */
@Data
@AllArgsConstructor
public class BusinessLogicException extends RuntimeException {

    /**
     * 异常类型
     */
    private RestStatus status;

    /**
     * 获取异常代码
     * @return 异常代码
     */
    public int getCode() {
        return status.getCode();
    }

    /**
     * 获取异常说明
     * @return 异常说明
     */
    public String getMessage() {
        return status.getMessage();
    }

}
