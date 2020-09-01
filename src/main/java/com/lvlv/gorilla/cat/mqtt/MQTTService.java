package com.lvlv.gorilla.cat.mqtt;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvlv.gorilla.cat.entity.sql.User;
import com.lvlv.gorilla.cat.service.MQTTMessageService;
import com.lvlv.gorilla.cat.service.UserService;
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

    @Autowired
    UserService userService;

    /**
     * 获取 App 端监听的接收消息的 topic
     *
     * @param uid
     * @return
     */
    public static String getAppListeningTopic(String uid) {
        return "cat/msg/" + uid;
    }

    /**
     * 解析 入口
     *
     * @param topic
     * @param payload
     */
    public void parseMessage(String topic, String payload) {

        if (MQTTClienter.TOPIC_LOGIN.equals(topic)) {
            userLogin(topic, payload);
        } else if (MQTTClienter.TOPIC_LOGOUT.equals(topic)) {
            userLogout(topic, payload);
        } else if (MQTTClienter.TOPIC_CAT_EAR.equals(topic)) {
            userMessage(topic, payload);
        }
    }

    /**
     * 处理用户上线消息
     *
     * @param topic
     * @param payload
     */
    private void userLogin(String topic, String payload) {
        if (StrUtil.isBlank(payload)) {
            return;
        }

        if (redisUtil.set(RedisKeyUtil.getMQTTOnlineKey(payload), "1")) {
            log.info("在 Redis 中设置 MQTT key 成功 uid = : " + payload);
        }
        ;

        // 发送未达的消息
        Long uid = Long.parseLong(payload);
        List<MQTTMessage> list = mqttMessageService.getUnsendMessage(uid);
        for (MQTTMessage message : list) {
            try {
                String forSend = mapper.writeValueAsString(message);
                if (mqttClienter.publish(getAppListeningTopic(payload), forSend)) {
                    mqttMessageService.setMessageSended(uid);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 处理用户下线消息
     *
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
     *
     * @param topic
     * @param payload
     */
    private void userMessage(String topic, String payload) {
        MQTTMessage mqttMessage;
        try {
            // 解析 App 消息并保存
            mqttMessage = mapper.readValue(payload, MQTTMessage.class);
            mqttMessage.setFlagSent(1);    // 由于 App 端可能没有设置此字段, 因此在此设置为已发送
            mqttMessageService.insertMessage(mqttMessage);

            if ("makeFriend".equals(mqttMessage.getCommand())) {
                makeFriend(mqttMessage);
            } else if ("makeFriendResponse".equals(mqttMessage.getCommand())) {
                makeFriendResponse(mqttMessage);
            } else {
                log.warn("undefined MQTT message type!");
            }
        } catch (Exception e) {
            log.error("Parsing MQTT user message error : " + e.getMessage());
        }
    }

    /**
     * 求加友请求 消息处理
     *
     * @param mqttMessage
     */
    private void makeFriend(MQTTMessage mqttMessage) {
        try {
            // 使用 app payload 构建 转发 payload
            Map<String, String> payloadObj = mapper.readValue(mqttMessage.getPayload(), Map.class);
            payloadObj.put("uid", mqttMessage.getSenderId().toString());

            // 把请求加友者的信息放进发给被请求者的 payload 中
            User user = userService.findUserByUid(mqttMessage.getSenderId());
            if (user != null) {
                payloadObj.put("nickname", user.getNickname());
                payloadObj.put("avatar", user.getAvatar());
                payloadObj.put("profile", user.getProfile());
            }

            String friendId = payloadObj.get("friendId");

            // 生成待转发的消息
            MQTTMessage message = new MQTTMessage();
            message.setType(2);  // 转发
            message.setCommand(mqttMessage.getCommand());
            message.setMsgId(mqttMessage.getMsgId());
            message.setReceiverId(Long.parseLong(friendId));
            message.setPayload(mapper.writeValueAsString(payloadObj));

            // 检查 friend 是否在线,如果在线转发 求加友 消息
            if (redisUtil.hasKey(RedisKeyUtil.getMQTTOnlineKey(friendId))) {
                String forSend = mapper.writeValueAsString(message);

                if (mqttClienter.publish(getAppListeningTopic(friendId), forSend)) {
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

    /**
     * 求加友响应 消息处理
     * @param mqttMessage
     */
    private void makeFriendResponse(MQTTMessage mqttMessage) {
        try {
            Map<String, String> payloadObj = mapper.readValue(mqttMessage.getPayload(), Map.class);
            String friendId = payloadObj.get("friendId");
            String result = payloadObj.get("result");

            // 生成待转发的消息
            MQTTMessage message = new MQTTMessage();
            message.setType(2);  // 转发
            message.setCommand(mqttMessage.getCommand());
            message.setMsgId(mqttMessage.getMsgId());
            message.setReceiverId(Long.parseLong(friendId));
            message.setPayload(mqttMessage.getPayload());

            // 检查 friend 是否在线,如果在线转发 求加友响应 消息
            if (redisUtil.hasKey(RedisKeyUtil.getMQTTOnlineKey(friendId))) {
                String forSend = mapper.writeValueAsString(message);

                if (mqttClienter.publish(getAppListeningTopic(friendId), forSend)) {
                    message.setSendTime(new Date());
                    message.setFlagSent(1);
                }
            }

            // 数据库中保存数据
            mqttMessageService.insertMessage(message);

            // 根据 result 保存 朋友关系
            // to-do

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Make friend error :" + e.getMessage());
        }
    }
}
