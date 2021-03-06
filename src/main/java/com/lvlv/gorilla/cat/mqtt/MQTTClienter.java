package com.lvlv.gorilla.cat.mqtt;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * MQTT 客户端
 */
@Component
@Slf4j
public class MQTTClienter implements ApplicationListener<ContextRefreshedEvent>, MqttCallback {

    // cat 监听之 topic : 客户端上线
    static final String TOPIC_LOGIN = "cat/login";

    // cat 监听之 topic : 客户端下线
    static final String TOPIC_LOGOUT = "cat/logout";

    // cat 监听之 topic : 客户端消息
    static final String TOPIC_CAT_EAR = "cat/ears";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MQTTService mqttService;

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

    @Value("${mqtt.getClientsUrl}")
    private String getClientsUrl;

    @Value("${mqtt.deleteClientUrl}")
    private String deleteClientUrl;

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
        this.subscribe(TOPIC_LOGIN);         // 监听客户端上线
        this.subscribe(TOPIC_LOGOUT);        // 监听客户端下线
        this.subscribe(TOPIC_CAT_EAR);       // 监听客户端消息

        // 获取当前客户信息
        getMQTTClients();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.info("MQTT连接丢失 ：{}", throwable.getMessage());

        this.close();
        this.initMQTT();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String payload = new String(mqttMessage.getPayload());
        //log.info("收到来自 {} 的消息：{}", topic, payload);

        mqttService.parseMessage(topic,payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("发布消息成功");
    }

    /**
     * 通过 Http Api 获取 MQTT 当前客户端
     */
    private void getMQTTClients() {
        ResponseEntity<String> responseEntity = restTemplate.exchange
                (getClientsUrl, HttpMethod.GET, new HttpEntity<String>(this.getMQTTHttpApiHeader()), String.class);

        log.info("++++++++++++++++++++++++++++++ MQTT get clients = {}", responseEntity.getBody());

        // To-do 针对每个 App 客户端进行相应的初始化工作，比如：更新 Redis 中 App 客户端 MQTT 在线状态等
    }

    /**
     * 从 EMQX 中踢掉 clientid
     * @param clientid
     */
    private void deleteClient(String clientid) {
        if (StrUtil.isBlank(clientid)) {
            log.warn("请求 MQTT 踢掉连接时无效的 clientid!");
            return;
        }
        ResponseEntity<String> responseEntity = restTemplate.exchange
                (getClientsUrl + clientid, HttpMethod.DELETE, new HttpEntity<String>(this.getMQTTHttpApiHeader()), String.class);

        log.info("++++++++++++++++++++++++++++++ MQTT delete client = {}", responseEntity.getBody());
    }

    /**
     * 创建访问 EMQX Http Api 的 header
     * @return
     */
    private HttpHeaders getMQTTHttpApiHeader() {
        String userMsg = userName + ":" + password;
        String base64UserMsg = "Basic " + Base64.encodeBase64String(userMsg.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", base64UserMsg);
        return headers;
    }

    //-------------------------------------------------------------------------------------------------
    //   以下为操作 MQTT 的公用方法
    //-------------------------------------------------------------------------------------------------

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
            options.setAutomaticReconnect(true);                //默认：false
            options.setCleanSession(true);                      //默认：true,如果设为 false, 掉线后 emqx 中的会话将保留 7200 秒,导致马上链接会失败
            options.setKeepAliveInterval(keepalive);            //默认：60

            mqttClient.setCallback(this);
            mqttClient.connect(options);

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
            log.error("MQTT publish error : " + e.getMessage());
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
