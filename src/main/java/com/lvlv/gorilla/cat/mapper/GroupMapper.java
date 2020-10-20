package com.lvlv.gorilla.cat.mapper;

import com.lvlv.gorilla.cat.entity.sql.GroupMain;
import com.lvlv.gorilla.cat.entity.sql.Grouper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupMapper {
    /**
     * 创建新 group
     * @param groupMain
     * @return
     */
    @Insert("insert into groupmain       "
            + "(groupId, tag, name, avatar, profile, createTime, updateTime, dismissTime, state)    "
            + "values                   "
            + "(#{groupId}, #{tag}, #{name}, #{avatar}, #{profile}, #{createTime}, #{updateTime}, #{dismissTime}, #{state}) ")
    int insertGroupMain(GroupMain groupMain);

    // -------------------------------------------------------------------------------------
    /**
     * 增加新群组成员
     * @param grouper
     * @return
     */
    @Insert("insert into grouper       "
            + "(groupId, uid, role, joinTime, updateTime, quitTime, state)    "
            + "values                   "
            + "(#{groupId}, #{uid}, #{role}, #{joinTime}, #{updateTime}, #{quitTime}, #{state}) ")
    int insertGrouper(Grouper grouper);

}
