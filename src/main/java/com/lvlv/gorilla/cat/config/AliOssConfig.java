package com.lvlv.gorilla.cat.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AliOssConfig {
    /**
     * 阿里云 oss 站点
     */
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    /**
     * 阿里云 oss 资源访问 url
     */
    @Value("${aliyun.oss.url}")
    private String url;

    /**
     * 阿里云公钥
     */
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    /**
     * 阿里云私钥
     */
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 阿里云 oss 文件根目录
     */
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Bean
    public OSS oSSClient() {
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

}
