package com.lvlv.gorilla.cat.util;

import com.lvlv.gorilla.cat.exception.BusinessLogicException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RestResult {
    /**
     * 结果编码：0 - 成功
     */
    private Integer code = 0;

    /**
     * 结果信息
     */
    private String message = "ok";

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 构造操作成功返回对象
     * @param data 待返回的数据对象
     */
    public RestResult(Object data) {
        this.data = data;
    }

    /**
     * 构造操作失败返回对象
     * @param code
     * @param message
     */
    public RestResult(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    /**
     * 根据业务逻辑异常创建操作失败返回对象
     * @param e 业务逻辑异常
     */
    public RestResult(BusinessLogicException e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    /**
     * 检查处理结果是否成功
     * @return 成功、失败标志
     */
    public boolean checkResult() {
        if (this.code == 0) {
            return true;
        } else {
            return false;
        }
    }

}
