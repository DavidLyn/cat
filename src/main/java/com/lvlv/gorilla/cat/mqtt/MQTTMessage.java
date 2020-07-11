package com.lvlv.gorilla.cat.mqtt;

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
    Date sendTime;          // 发送时间
}
