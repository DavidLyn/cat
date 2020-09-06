package com.lvlv.gorilla.cat.mapper;

import com.lvlv.gorilla.cat.entity.sql.Follower;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowerMapper {

    /**
     * 添加 关注 关系
     * @param uid
     * @param followerId
     * @return
     */
    @Insert( "insert into follower " +
            "(uid, followerId) values " +
            "(#{uid}, #{followerId})" )
    int insertFollower( @Param("uid") long uid,
                        @Param("followerId") long followerId );

    /**
     * 获取某用户的粉丝数
     * @param uid
     * @return
     */
    @Select("select count(uid) from follower where state = 1 and uid = #{uid}")
    int getFanCount(@Param("uid") long uid);

    /**
     * 获取某用户的粉丝列表
     * @param uid
     * @return
     */
    @Select("select id, uid, followerId, state, followTime, deleteTime from follower where state = 1 and uid = #{uid}")
    List<Follower> getFunList(@Param("uid") long uid);

    /**
     * 获取某用户的关注数
     * @param followerId
     * @return
     */
    @Select("select count(followerId) from follower where state = 1 and followerId = #{followerId}")
    int getFollowCount(@Param("followerId") long followerId);

    /**
     * 获取某用户的关注列表
     * @param followerId
     * @return
     */
    @Select("select id, uid, followerId, state, followTime, deleteTime from follower where state = 1 and followerId = #{followerId}")
    List<Follower> getFollowList(@Param("followerId") long followerId);

    /**
     * 取消关注
     * @param uid
     * @param followerId
     * @return
     */
    @Update("update follower set state = 0, deleteTime = now()  where uid = #{uid} and followerId = #{followerId}")
    int deleteFollow( @Param("uid") long uid,
                      @Param("followerId") long followerId );

}
