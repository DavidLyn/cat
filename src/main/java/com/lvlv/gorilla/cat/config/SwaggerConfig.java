package com.lvlv.gorilla.cat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfig {
    /**
     * 扫描 controler 的包名
     */
    @Value("${swagger.basePackage}")
    private String basePackage;

    /**
     * 在线文档的标题
     */
    @Value("${swagger.title}")
    private String title;

    /**
     * 在线文档的描述
     */
    @Value("${swagger.description}")
    private String description;

    /**
     * 联系人
     */
    @Value("${swagger.contact_name}")
    private String contactName;

    /**
     * 联系 url
     */
    @Value("${swagger.contact_url}")
    private String contactUrl;

    /**
     * 联系 email
     */
    @Value("${swagger.contact_email}")
    private String contactEmail;

    /**
     * 文档版本
     */
    @Value("${swagger.version}")
    private String version;

    /**
     * license url
     */
    @Value("${swagger.license_url}")
    private String licenseUrl;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact(contactName,contactUrl,contactEmail))
                        .licenseUrl(licenseUrl)
                        .build());
    }

}
