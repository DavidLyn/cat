package com.lvlv.gorilla.cat.util;

/**
 * 定义业务类错误代码和说明（参考 HttpStatus 实现）
 * @author lvweiwei
 * @date 2020-05-02 19:00
 */
public enum RestStatus {

    SERVER_ERROR(-1,"server error"),
    AUTHENTICATION_NO_TOKEN(2001,"no token"),
    AUTHENTICATION_INVALID_TOKEN(2002,"invalid token"),
    AUTHENTICATION_INVALID_USER(2003,"invalid user"),
    AUTHENTICATION_VERIFIED_FAILED(2004,"token verified failed");

    private final int code;
    private final String message;

    private RestStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
