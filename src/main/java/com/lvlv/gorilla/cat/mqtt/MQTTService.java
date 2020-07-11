package com.lvlv.gorilla.cat.mqtt;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvlv.gorilla.cat.service.MQTTMessageService;
import com.lvlv.gorilla.cat.util.RedisKeyUtil;
import com.lvlv.gorilla.cat.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MQTTService {

    // Jackson ObjectMapper 据说是线程安全的
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MQTTClienter mqttClienter;

    @Autowired
    MQTTMessageService mqttMessageService;

    /**
     * 获取 App 端监听的接收消息的 topic
     * @param uid
     * @return
     */
    public static String getAppListeningTopic(String uid) {
        return "cat/msg/" + uid;
    }

    /**
     * 解析 入口
     * @param topic
     * @param payload
     */
    public void parseMessage(String topic, String payload) {

        if (MQTTClienter.TOPIC_LOGIN.equals(topic)) {
            userLogin(topic,payload);
        } else if (MQTTClienter.TOPIC_LOGOUT.equals(topic)) {
            userLogout(topic,payload);
        } else if (MQTTClienter.TOPIC_CAT_EAR.equals(topic)) {
            userMessage(topic,payload);
        }
    }

    /**
     * 处理用户上线消息
     * @param topic
     * @param payload
     */
    private void userLogin(String topic, String payload) {
        if (StrUtil.isBlank(payload)) {
            return;
        }

        if (redisUtil.set(RedisKeyUtil.getMQTTOnlineKey(payload),"1")){
            log.info("在 Redis 中设置 MQTT key 成功 uid = : " + payload);
        };

        // 发送未达的消息
        Long uid = Long.parseLong(payload);
        List<MQTTMessage> list = mqttMessageService.getUnsendMessage(uid);
        for (MQTTMessage message : list) {
            try {
                String forSend = mapper.writeValueAsString(message);
                if (mqttClienter.publish(getAppListeningTopic(payload),forSend)) {
                    mqttMessageService.setMessageSended(uid);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 处理用户下线消息
     * @param topic
     * @param payload
     */
    private void userLogout(String topic, String payload) {
        if (StrUtil.isBlank(payload)) {
            return;
        }

        redisUtil.del(RedisKeyUtil.getMQTTOnlineKey(payload));

        log.info("在 Redis 中删除 MQTT key 成功 uid = " + payload);
    }

    /**
     * 处理用户消息
     * @param topic
     * @param payload
     */
    private void userMessage(String topic, String payload) {
        MQTTMessage mqttMessage;
        try {
            mqttMessage = mapper.readValue(payload,MQTTMessage.class);

            if ("makeFriend".equals(mqttMessage.getCommand())) {
                makeFriend(mqttMessage);
            } else {
                log.warn("undefined MQTT message type!");
            }
        } catch (Exception e) {
            log.error("Parsing MQTT user message error : " + e.getMessage());
        }
    }

    /**
     * 交友消息处理
     * @param mqttMessage
     */
    private void makeFriend(MQTTMessage mqttMessage) {
        try {
            // 使用 客户端 payload 构建 转发 payload
            Map<String, String> payloadObj = mapper.readValue(mqttMessage.getPayload(), Map.class);
            payloadObj.put("uid",mqttMessage.getSenderId().toString());

            String friendUid = payloadObj.get("friendId");

            // 生成待转发的消息
            MQTTMessage message = new MQTTMessage();
            message.setType(2);  // 转发
            message.setCommand(mqttMessage.getCommand());
            message.setReceiverId(Long.parseLong(friendUid));
            message.setPayload(mapper.writeValueAsString(payloadObj));

            // 检查 friend 是否在线,如果在线转发 求加友 消息
            if (redisUtil.hasKey(RedisKeyUtil.getMQTTOnlineKey(friendUid))) {
                String forSend = mapper.writeValueAsString(message);

                if (mqttClienter.publish(getAppListeningTopic(friendUid),forSend)) {
                    message.setSendTime(new Date());
                    message.setFlagSent(1);
                }
            }

            // 数据库中保存数据
            mqttMessageService.insertMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Make friend error :" + e.getMessage());
        }
    }

}
