package com.lvlv.gorilla.cat.mqtt;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class MQTTMessage {
    int id;
    int type = 0;           // 0 - request   1 - response   2 - relay
    String command;         // 命令字
    String msgId;           // 消息id : uuid
    Long senderId = new Integer(0).longValue();          // 发送者id, 0 - cat   其他 - uid
    Long receiverId;        // 接收者id, 0 - cat   其他 - uid
    String payload;         // 负载,对象转为 Json
    int flagSent = 0;       // 已发送标志 0 - 未发送  1 - 已发送

    // 必须使用下述注解,不然 Jackson 解析 Json 时会报错
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date sendTime;          // 发送时间
}
