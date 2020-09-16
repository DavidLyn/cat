package com.lvlv.gorilla.cat.mapper;

import com.lvlv.gorilla.cat.entity.sql.Friend;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendMapper {

    /**
     * 获取 朋友关系 计数,用于检查是否已建立 朋友 关系
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
     * @param friendId
     * @return
     */
    @Update("update friend set state = 0, deleteTime = now(), updateTime = now()  where uid = #{uid} and friendId = #{friendId}")
    int deleteFriend( @Param("uid") long uid,
                      @Param("friendId") long friendId );

    /**
     * 取得好友列表
     * @param uid
     * @return
     */
    @Select("select id, uid , friendId, relation, state, friendTime, deleteTime from friend where uid = #{uid}")
    List<Friend> getFriends(@Param("uid") long uid);

    /**
     * 修改 关系
     * @param uid
     * @param friendId
     * @param relation
     * @return
     */
    @Update("update friend set relation = #{relation}, updateTime = now()  where uid = #{uid} and friendId = #{friendId}")
    int updateRelation( @Param("uid") long uid,
                        @Param("friendId") long friendId,
                        @Param("relation") String relation );

}
