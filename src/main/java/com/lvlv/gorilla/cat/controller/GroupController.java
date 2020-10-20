package com.lvlv.gorilla.cat.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lvlv.gorilla.cat.entity.sql.GroupMain;
import com.lvlv.gorilla.cat.entity.sql.Grouper;
import com.lvlv.gorilla.cat.service.GroupService;
import com.lvlv.gorilla.cat.service.tool.AliOssService;
import com.lvlv.gorilla.cat.util.MysqlUtil;
import com.lvlv.gorilla.cat.util.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供与 社交(群组) 相关的服务
 */
@RestController
@RequestMapping("/group")
@Slf4j
public class GroupController {
    @Autowired
    GroupService groupService;

    @Autowired
    private AliOssService aliOssService;

    /**
     * 创建新的 群组
     * @param uid
     * @param name
     * @param profile
     * @param file
     * @return
     */
    @PostMapping("/createGroup")
    public RestResult createGroup( @RequestParam(value = "uid", required = true) Long uid,
                                   @RequestParam(value = "name", required = true) String name,
                                   @RequestParam(value = "profile", required = true) String profile,
                                   @RequestParam("file") MultipartFile file ) {
        RestResult result = new RestResult();

        if (StrUtil.isBlank(name)) {
            result.setCode(-1);
            result.setMessage("Invalid parameters");
            return result;
        }

        // 向 阿里云 oss 上传文件
        // 获取上载文件原始名称
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isEmpty(originalFilename)) {
            result.setCode(-1);
            result.setMessage("Invalid upload file name");
            return result;
        }

        // 获取原始文件后缀并生成新文件名
        String type = "";

        if (originalFilename.lastIndexOf(".") >= 0) {
            type = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        String newFileName = IdUtil.simpleUUID();

        if (StrUtil.isNotEmpty(type)) {
            newFileName = newFileName + "." + type;
        }

        // 上传文件至 Aliyun oss
        String newAvatarFileUrl = aliOssService.uploadToOSS( newFileName, file );

        if (StrUtil.isEmpty(newAvatarFileUrl)) {
            result.setCode(-1);
            result.setMessage("Upload to ass error");
            return result;
        }

        // 创建 GroupMain
        GroupMain groupMain = new GroupMain();
        groupMain.setAvatar(newAvatarFileUrl);
        groupMain.setName(name);
        groupMain.setProfile(profile);
        groupMain.setGroupId(MysqlUtil.getNextGroupId());

        Grouper grouper = new Grouper();
        grouper.setGroupId(groupMain.getGroupId());
        grouper.setRole(0);    // 群主
        grouper.setUid(uid);

        try {
            groupService.insetGroup(groupMain,grouper);
        } catch (Exception e) {
            aliOssService.deleteFromOSS(newAvatarFileUrl);

            result.setCode(-1);
            result.setMessage("Create new group error");
            return result;
        }

        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("avatar",newAvatarFileUrl);
        resultMap.put("groupId",groupMain.getGroupId());

        result.setData(resultMap);

        return result;
    }

}
