# 说明

> **EMQX的开源版本不能持久化消息，不能持久化规则**

# 消息格式定义,采用 Json 格式

```
{
  "type":0/1                          // 0 - request   1 - response
  "command":"xxxxxxx"                 // 命令字
  "msgId":"xxxxxxx-xxxxx-xxxx"        // uuid
  "sendTime":"xxxx-xx-xx xx:xx:xx"    // 发送时间
  "payload":"Json string"             // 负载,对象转为 Json
}
```

# 消息上报主题

> cat 通过订阅 cat/ears 接收客户端发送的消息
>
> 客户端如有消息需要上报向 cat/ears 发送消息

# 消息下发主题

> 客户端通过订阅 cat/msg/{uid} 接收 cat 发送的消息
> 
> cat 通过 cat/msg/{uid} 向 uid 下发消息

# 定义规则

## 转发客户端连接消息至 cat/login

+ 移动端使用 uid 作为 clientid

+ cat 通过订阅 cat/login 来判断连接至 EMQX 的用户是否合法

> 如果不合法通过调用 EMQX 的 http 接口将其连接踢掉
>
> 如果合法将其进行缓存，cat 据此向 cat/msg/{uid} 主题实时向其发送各种消息，否则将消息缓存

+ 规则 SQL

```
SELECT
  clientid
FROM 
  "$events/client_connected"
```

+ 响应动作

> 动作类型：消息重新发布
>
> 目的主题：cat/login
>
> 消息内容模板：${clientid}

## 转发客户端连接消息至 repub/logout

> cat 据此修改客户端的在线状态

+ 规则 SQL

```
SELECT
  clientid
FROM 
   "$events/client_connected"
```

+ 响应动作

> 动作类型：消息重新发布
>
> 目的主题：cat/logout
>
> 消息内容模板：${clientid}

# Http Api

+ 踢掉连接

> DELETE /api/v4/clients/{clientid} 

+ 返回集群下所有客户端的信息，支持分页

> GET /api/v4/clients