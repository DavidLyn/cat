package com.lvlv.gorilla.cat.service;

import com.lvlv.gorilla.cat.entity.sql.GroupMain;
import com.lvlv.gorilla.cat.entity.sql.Grouper;
import com.lvlv.gorilla.cat.mapper.GroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupService {
    @Autowired
    GroupMapper groupMapper;

    /**
     * 创建新 群组
     * @param groupMain
     * @param grouper
     */
    public void insetGroup(GroupMain groupMain, Grouper grouper) {
        groupMapper.insertGroupMain(groupMain);
        groupMapper.insertGrouper(grouper);
    }

}
