package com.lvlv.gorilla.cat.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestResult {
    /**
     * 结果编码：0 - 成功
     */
    private Integer code = 0;

    /**
     * 结果信息
     */
    private String message = "";

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
     * 处理结果是否成功
     * @return 成功、失败标志
     */
    public boolean isSuccess() {
        if (this.code == 0) {
            return true;
        } else {
            return false;
        }
    }
}
