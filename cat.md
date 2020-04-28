---
# 导入包

## Druid

+ pom

```
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.11</version>
        </dependency>

        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>

```
> 如果不导入 log4j ，运行时会报错

+ 添加配置类 com.lvlv.gorilla.cat.config.DruidConfig

+ 监控 Druid

> http://127.0.0.1:8080/druid/index.html

## [Hutool](https://hutool.cn/docs/#/)

+ pom

```
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.3.2</version>
</dependency>
```

## MongoDB

+ pom

```$xslt
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

+ 修改 CatApplication

> 将 @SpringBootApplication 改为

```$xslt
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
```

> 不然启动时会报 mongodb 打开 socket 失败的异常

## Redis

---
# 其他
## 后台架构
+ Spring Security
+ SpringBoot
+ Redis
+ JWT(OATH2.0)

## [不要用JWT替代session管理](http://ju.outofmemory.cn/entry/360732)


## Spring Session
+ [Spring Session 中文文档](https://www.springcloud.cc/spring-session.html)


## Spring Data JPA
+ [Spring Data Jpa的使用](https://www.jianshu.com/p/c23c82a8fcfc)
+ 
