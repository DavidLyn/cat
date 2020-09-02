package com.lvlv.gorilla.cat.mapper;

import com.lvlv.gorilla.cat.entity.sql.Friend;
import org.apache.ibatis.annotations.*;

@Mapper
public interface FriendMapper {

    /**
     * 获取 朋友关系 计数
     * @param uid
     * @param friendId
     * @return
     */
    @Select("select count(uid) from friend where uid = #{uid} and friendId = #{friendId} and state = 1")
    int getFriendCount( @Param("uid") long uid,
                        @Param("friendId") long friendId );

    /**
     * 添加好友关系
      * @param uid
     * @param friendId
     * @return
     */
    @Insert( "insert into friend " +
             "(uid, friendId) values " +
             "(#{uid}, #{friendId})" )
    int insertFriend( @Param("uid") long uid,
                      @Param("friendId") long friendId );

    /**
     * 删除/拉黑 好友
     * @param uid
     * @return
     */
    @Update("update friend set state = 0, deleteTime = now()  where uid = #{uid}")
    int deleteFriend(@Param("uid") long uid);
}
