package com.lvlv.gorilla.cat.service.tool;

import com.aliyun.oss.OSS;
import com.lvlv.gorilla.cat.config.AliOssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;

@Service
@Slf4j
public class AliOssService {

    @Autowired
    private AliOssConfig aliOssConfig;

    @Autowired
    private OSS ossClient;

    /**
     * 上传文件到 Aliyun OSS
     * @param fileName 写入 oss 的文件名
     * @param uploadFile 文件流
     * @return 可访问 url
     */
    public String uploadToOSS(String fileName, MultipartFile uploadFile) {

        try {
            ossClient.putObject(aliOssConfig.getBucketName(), fileName, new
                    ByteArrayInputStream(uploadFile.getBytes()));
        } catch (Exception e) {
            log.error("upload to aliyun oss error : " + e.getMessage());
            return null;
        }

        return aliOssConfig.getUrl() + fileName;
    }

    /**
     * 用于测试上传到本地文件系统
     * @param fileName
     * @param file
     * @return
     */
    public String uploadToLocal(String fileName, MultipartFile file) {
        String realPath = "/Users/lvweiwei/catlog/" + fileName;

        File dest = new File(realPath);

        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }

        try {
            file.transferTo(dest);
            return realPath;
        } catch (Exception e) {
            log.error("upload file to local error : " + e.getMessage());
            return null;
        }
    }

}
