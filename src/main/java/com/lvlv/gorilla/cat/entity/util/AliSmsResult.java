package com.lvlv.gorilla.cat.entity.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AliSmsResult {

    @JsonProperty("Message")
    private String message;

    @JsonProperty("RequestId")
    private String requestId;

    @JsonProperty("BizId")
    private String bizId;

    @JsonProperty("Code")
    private String code;
}
