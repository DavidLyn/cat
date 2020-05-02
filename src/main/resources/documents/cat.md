---
# 版本管理

> 在 https://github.com/DavidLyn 上创建 repository cat
>
> 注意：下述不能用 git@github.com:DavidLyn/cat.git

```
git remote add origin https://github.com/DavidLyn/cat.git
git push -u origin master
```

---
# 导入包

## Mysql

+ application.yml

```
url: jdbc:mysql://127.0.0.1:3306/catdb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
```

> 注意：加上 `&serverTimezone=Asia/Shanghai` 是为避免存入数据库的时间与实际时间差 13 个小时的问题

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

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

+ 工具类

> 感觉设置工具类也没有太大的意思 
>
> [思路一：封装成模版类](https://blog.csdn.net/qq_22041375/article/details/95112022)
>
> [思路二：封装成静态工具类](https://blog.csdn.net/qq_37421862/article/details/81287247?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-6&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-6)

## Redis

+ 参考资料：[springboot redis 项目实战 完整篇](https://www.jianshu.com/p/5596c3a4978d)

+ pom

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

+ application-dev.yml

```
  redis:
    host: localhost
    port: 6379
    max-wait: 30000     # 连接池最大阻塞等待时间(使用负值表示没有限制)
    max-active: 100     # 连接池最大连接数(使用负值表示没有限制)
    max-idle: 20        # 连接池中最大空闲连接数
    min-idle: 0         # 连接池中最小空闲连接数
    timeout: 3000       # 连接超时
```

+ 增加配置类和工具类

> 配置类：com.lvlv.gorilla.cat.config.RedisConfig
>
> 工具类：com.lvlv.gorilla.cat.util.RedisUtil

## Mybatis

+ 参考资料

> [SpringBoot整合Mybatis完整详细版](https://blog.csdn.net/kangkangwanwan/article/details/91577261)
>
> [SpringBoot + MyBatis(注解版)，常用的SQL方法](https://www.cnblogs.com/caizhaokai/p/10982727.html)

+ pom

```
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.0.1</version>
</dependency>
```

+ 在 resources下创建新目录 mapping，用于放置 mapper xml 文件

+ application-dev.yml

```
mybatis:
  mapper-locations: classpath:mapping/*.xml    # mapper xml 文件地址
  type-aliases-package: com.lvlv.gorilla.cat.entity.sql     # 实体类包路径

logging:
  level:
    com.lvlv.gorilla.cat.mapper: debug     # 标准输出会显示sql语句
```

## jwt

+ 参考资料

> [SpringBoot集成JWT实现token验证](https://www.jianshu.com/p/e88d3f8151db)

+ pom

```
<dependency>
    <groupId>com.auth0</groupId>
     <artifactId>java-jwt</artifactId>
     <version>3.4.0</version>
</dependency>
```
+ 增加两个注解

> com.lvlv.gorilla.cat.annotation.PassToken : 跳过验证
>
> com.lvlv.gorilla.cat.annotation.UserLoginToken : 需要登录才能进行操作

+ 增加拦截器和相关的配置类

> com.lvlv.gorilla.cat.interceptor.AuthenticationInterceptor
> 
> com.lvlv.gorilla.cat.config.InterceptorConfig

## 统一返回和统一异常处理

+ 创建 RestResult 类

> com.lvlv.gorilla.cat.util.RestResult

+ 创建业务逻辑异常类

> com.lvlv.gorilla.cat.exception.BusinessLogicException

+ 创建统一异常处理类

> com.lvlv.gorilla.cat.exception.GlobalExceptionHandler

## https

## swagger

---
# 其他

## Spring Session
+ [Spring Session 中文文档](https://www.springcloud.cc/spring-session.html)

## Spring Data JPA
+ [Spring Data Jpa的使用](https://www.jianshu.com/p/c23c82a8fcfc)

## 鉴权与认证
+ [SpringBoot集成JWT实现token验证 -- 可用](https://www.jianshu.com/p/e88d3f8151db)

+ [微服务架构下的安全认证与鉴权](https://www.jianshu.com/p/15d0a1c366b3)

+ [WEB后台--基于Token的WEB后台登录认证机制](https://www.jianshu.com/p/227306fa28e4)

+ [app与后台交互之间的几种安全认证机制](https://www.jianshu.com/p/0d6c8f38f167)

+ [App中用户验证方案](http://www.dczou.com/viemall/434.html)

+ [JWT的Java使用 (JJWT)](https://blog.csdn.net/qq_37636695/article/details/79265711)

## 统一返回和统一异常处理

+ [SpringBoot2.0统一返回Rest风格数据结构与统一异常处理](https://www.jianshu.com/p/9ff254413e9d)

+ [统一异常处理和统一数据返回](https://www.jianshu.com/p/dda85b16bf8a)

+ [web应用之统一异常及返回值处理](https://zhuanlan.zhihu.com/p/38196945)

+ [Spring Boot中Web应用的统一异常处理](http://blog.didispace.com/springbootexception/)

## 密码管理

+ [密码存储与传输的那些事儿（一）密码存储概述](https://blog.csdn.net/jjxojm/article/details/81545407?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-3&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-3)

+ [如何安全地存储密码](https://blog.csdn.net/cadcisdhht/article/details/19282407)
