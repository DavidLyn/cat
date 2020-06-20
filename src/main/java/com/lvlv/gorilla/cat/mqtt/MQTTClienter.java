package com.lvlv.gorilla.cat.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * MQTT 客户端
 */
@Component
@Slf4j
public class MQTTClienter implements ApplicationListener<ContextRefreshedEvent>, MqttCallback {

    @Value("${mqtt.userName}")
    private String userName;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.url}")
    private String url;

    @Value("${mqtt.clientID}")
    private String clientID;

    @Value("${mqtt.defaultTopic}")
    private String defaultTopic;

    @Value("${mqtt.timeout}")
    private int timeout;

    @Value("${mqtt.keepalive}")
    private int keepalive;

    private MqttClient mqttClient;

    /**
     * 系统启动后建立与 MQTT broker 的连接 订阅相应的主题
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 防止执行两次 https://www.jianshu.com/p/4bd3f68cb179
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        initMQTT();
    }

    /**
     * 连接 MQTT 进行订阅初始化
     */
    private void initMQTT() {
        // 连接 MQTT broker
        this.connect();

        // TODO  此处对订阅进行初始化
        this.subscribe("test");
        this.subscribe("login");
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.info("断开了MQTT连接 ：{}", throwable.getMessage());
        //log.error(throwable.getMessage(), throwable);
        this.initMQTT();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        //  TODO  此处可以将订阅得到的消息进行业务处理、数据存储
        log.info("收到来自 {} 的消息：{}", topic, new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("发布消息成功");
    }

    /**
     * 连接 MQTT broker
     *
     * @return
     */
    public boolean connect() {
        try {
            mqttClient = new MqttClient(url, clientID, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(userName);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(timeout);              //默认：30 s
            options.setCleanSession(false);                     //默认：true
            // options.setAutomaticReconnect(true);                //默认：false
            options.setCleanSession(false);                     //默认：true
            options.setKeepAliveInterval(keepalive);            //默认：60

            mqttClient.setCallback(this);
            mqttClient.connect(options);

            log.info("####################################### connect mqtt ok!");
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    /**
     * 断开与 MQTT broker 的连接
     *
     * @throws MqttException
     */
    public boolean close() {
        try {
            mqttClient.close();
            mqttClient.disconnect();
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    /**
     * 以默认 Oos 1 向设定主题发布消息
     *
     * @param topic
     * @param msg
     * @return
     */
    public boolean publish(String topic, String msg) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(msg.getBytes());
            MqttTopic mqttTopic = mqttClient.getTopic(topic);
            MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
            token.waitForCompletion();

            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    /**
     * 向设定主题发布消息,可设置 Qos
     *
     * @param topic
     * @param msg
     * @param qos
     * @return
     */
    public boolean publish(String topic, String msg, int qos) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(msg.getBytes());
            mqttMessage.setQos(qos);
            MqttTopic mqttTopic = mqttClient.getTopic(topic);
            MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
            token.waitForCompletion();

            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    /**
     * 订阅某一个主题 默认 Qos 1
     *
     * @param topic
     * @return
     * @throws MqttException
     */
    public boolean subscribe(String topic) {
        try {
            mqttClient.subscribe(topic);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    /**
     * 订阅某一个主题，可设置Qos
     *
     * @param topic
     * @param qos
     * @return
     */
    public boolean subscribe(String topic, int qos) {
        try {
            mqttClient.subscribe(topic, qos);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    /**
     * 取消订阅
     *
     * @param topic
     * @throws MqttException
     */
    public boolean unSubscribe(String topic) {
        try {
            mqttClient.unsubscribe(topic);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

}
