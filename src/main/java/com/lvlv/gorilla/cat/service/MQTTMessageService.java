package com.lvlv.gorilla.cat.service;

import com.lvlv.gorilla.cat.mapper.MQTTMessageMapper;
import com.lvlv.gorilla.cat.mqtt.MQTTMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MQTTMessageService {
    @Autowired
    MQTTMessageMapper mqttMessageMapper;

    /**
     * 保存 mqtt 消息
     * @param mqttMessage
     */
    public void insertMessage(MQTTMessage mqttMessage) {
        mqttMessageMapper.insertMessage(mqttMessage);
    }

    /**
     * 取得设定用户的未发送消息
     * @param uid
     * @return
     */
    public List<MQTTMessage> getUnsendMessage(long uid) {
        return mqttMessageMapper.getUnsendMessage(uid);
    }

    /**
     * 设置已发送
     * @param uid
     */
    public void setMessageSended(long uid) {
        mqttMessageMapper.setMessageSended(uid, new Date());
    }
}
