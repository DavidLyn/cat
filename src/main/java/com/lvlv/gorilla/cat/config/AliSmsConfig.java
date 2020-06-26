package com.lvlv.gorilla.cat.config;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvlv.gorilla.cat.entity.util.AliSmsResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@Slf4j
public class AliSmsConfig {
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
     * 阿里云 sms 模版 code
     */
    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

    /**
     * 阿里云 sms 签名名称
     */
    @Value("${aliyun.sms.signName}")
    private String signName;

    @Autowired
    IAcsClient client;

    @Bean
    DefaultProfile defaultProfile() {
        return DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    }

    @Bean
    IAcsClient iAcsClient(DefaultProfile defaultProfile) {
        return new DefaultAcsClient(defaultProfile);
    }

    public boolean sendSms(String mobile, String vCode) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");  //不要改
        request.setSysVersion("2017-05-25");            //不要改
        request.setSysAction("SendSms");                //sendSms
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",vCode);

        try {
            ObjectMapper mapper = new ObjectMapper();
            request.putQueryParameter("TemplateParam", mapper.writeValueAsString(map));
            CommonResponse response = client.getCommonResponse(request);
            AliSmsResult result = mapper.readValue(response.getData(),AliSmsResult.class);

            log.info("-------------------------------> sendSms result:" + response.getData());
            if ("OK".equalsIgnoreCase(result.getCode())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("-------------------------------> sendSms Exception:" + e.getMessage());
            return false;
        }
    }
}
