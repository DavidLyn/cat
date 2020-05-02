package com.lvlv.gorilla.cat.exception;

import com.lvlv.gorilla.cat.util.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常统一处理
 * @author lvweiwei
 * @date 2020-05-02 13:57
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RestResult handleException(Exception e) {

        if (e instanceof BusinessLogicException) {
            BusinessLogicException ble = (BusinessLogicException) e;

            return new RestResult(ble.getCode(), ble.getErrorMessage());
        } else {
            return new RestResult( BusinessLogicException.SERVER_ERROR,
                                   BusinessLogicException.getServerErrorMessage() );
        }
    }

}
