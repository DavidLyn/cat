# 安装 Docker plugin

使用下述菜单：

[IntelliJ IDEA] -> 【Plugins】

# 在 IntelliJ IDEA 添加 Docker Server

使用下述菜单：

[IntelliJ IDEA] -> [Preferences] -> [Build,Execution,Depolyment] -> [Docker] -> [+]

如果是远程 Docker Server，如下设置 TCP Socket：

```
tcp://47.94.248.253:2375
```

# 创建 Dockerfile

在项目根目录下创建 Dockerfile，如下：

```
FROM java:openjdk-8-jdk-alpine
ADD target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

# 设置 ```Run/Debug Configuration```

使用下述菜单：

[Edit Configurations] -> [Docker] -> [+]