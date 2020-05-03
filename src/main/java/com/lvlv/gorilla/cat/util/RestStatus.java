package com.lvlv.gorilla.cat.util;

import org.springframework.http.HttpStatus;

/**
 * 定义应用级错误代码和错误信息，以及所属 HTTP 状态码（参考 HttpStatus 实现）
 * @author lvweiwei
 * @date 2020-05-02 19:00
 */
public enum RestStatus {

    SERVER_ERROR(20000,"server error", HttpStatus.INTERNAL_SERVER_ERROR),
    AUTHENTICATION_NO_TOKEN(20001,"no token",HttpStatus.GONE),
    AUTHENTICATION_INVALID_TOKEN(20002,"invalid token",HttpStatus.GONE),
    AUTHENTICATION_INVALID_USER(20003,"invalid user",HttpStatus.GONE),
    AUTHENTICATION_VERIFIED_FAILED(20004,"token verified failed",HttpStatus.GONE);

    /**
     * 应用级错误代码
     */
    private final int code;

    /**
     * 应用级错误信息
     */
    private final String message;

    /**
     * Http 状态码
     */
    private final HttpStatus status;

    private RestStatus(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
