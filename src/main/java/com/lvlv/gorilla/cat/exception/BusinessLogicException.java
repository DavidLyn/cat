package com.lvlv.gorilla.cat.exception;

import com.lvlv.gorilla.cat.util.RestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * 业务逻辑异常类
 * @author lvweiwei
 * @date 2020-05-02 01:30
 */
@Data
@AllArgsConstructor
@Slf4j
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


    /**
     * 获取 Http 状态
     * @return Http 状态
     */
    public HttpStatus getStatus() {
        return status.getStatus();
    }

}
