package com.lvlv.gorilla.cat.exception;

import com.lvlv.gorilla.cat.util.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常统一处理
 * @author lvweiwei
 * @date 2020-05-02 13:57
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<RestResult> handleException(Exception e) {

        if (e instanceof BusinessLogicException) {
            BusinessLogicException ble = (BusinessLogicException) e;

            return new ResponseEntity<RestResult>( new RestResult(ble),
                                                   ble.getStatus());
        } else {
            return new ResponseEntity<RestResult>((RestResult)null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
