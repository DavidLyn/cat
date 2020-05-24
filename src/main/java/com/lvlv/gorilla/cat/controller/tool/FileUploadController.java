package com.lvlv.gorilla.cat.controller.tool;

import cn.hutool.core.util.IdUtil;
import com.lvlv.gorilla.cat.service.tool.AliOssService;
import com.lvlv.gorilla.cat.util.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 通用 文件上传 controller 可能没有什么用
 */
@RestController
@Slf4j
public class FileUploadController {

    @Autowired
    private AliOssService aliOssService;

    @PostMapping("/fileUpload")
    public RestResult upload(@RequestParam("file") MultipartFile file) {

        RestResult result = new RestResult();

        String fileName = IdUtil.simpleUUID() ;

        if (!writeToLocal(file,"/Users/lvweiwei/catlog",fileName)) {
            result.setCode(-1);
            result.setMessage("upload error");
        }

        return result;
    }

    // 将文件写入本地文件系统
    private static boolean writeToLocal(MultipartFile file, String path, String fileName) {
        String realPath = path + "/" + fileName;

        File dest = new File(realPath);

        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }

        try {
            file.transferTo(dest);
            return true;
        } catch (Exception e) {
            log.error("upload file error : " + e.getMessage());
            return false;
        }
    }

}
