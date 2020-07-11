package com.lvlv.gorilla.cat.mapper;

import com.lvlv.gorilla.cat.mqtt.MQTTMessage;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface MQTTMessageMapper {

    /**
     * 插入新消息
     * @param message
     * @return
     */
    @Insert("insert into mqttmessage       "
            + "(type, command, msgId, payload, senderId, receiverId, flagSent, sendTime)    "
            + "values                   "
            + "(#{type}, #{command}, #{msgId}, #{payload}, #{senderId}, #{receiverId}, #{flagSent}, #{sendTime}) ")
    int insertMessage(MQTTMessage message);

    /**
     * 取得设定用户的未发送消息
     * @param uid
     * @return
     */
    @Select("select id, type, command, msgId, payload, senderId, receiverId, flagSent, sendTime  "
            + "from mqttmessage "
            + "where receiverId = #{uid} ")
    List<MQTTMessage> getUnsendMessage(@Param("uid") long uid);

    /**
     * 设置消息为已发送
     * @param id
     * @param sendTime
     * @return
     */
    @Update("update mqttmessage set  "
            + "flagSent  = 1,    "
            + "sendTime = #{sendTime}"
            + "where id = #{id} ")
    int setMessageSended(@Param("id") long id,
                         @Param("sendTime") Date sendTime);

}
