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
    @Insert("insert into groupmain "
            + "(groupId, name, avatar, profile) "
            + "values "
            + "(#{groupId}, #{name}, #{avatar}, #{profile}) ")
    int insertGroupMain(GroupMain groupMain);

    // -------------------------------------------------------------------------------------
    /**
     * 增加新群组成员
     * @param grouper
     * @return
     */
    @Insert("insert into grouper "
            + "(groupId, uid, role)    "
            + "values                   "
            + "(#{groupId}, #{uid}, #{role}) ")
    int insertGrouper(Grouper grouper);

}
