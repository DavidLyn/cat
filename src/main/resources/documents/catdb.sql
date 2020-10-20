DROP DATABASE IF EXISTS catdb;
CREATE DATABASE catdb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE catdb;

-- 用户表
DROP TABLE IF EXISTS user;

CREATE TABLE user(
  uid bigint NOT NULL COMMENT '用户ID',
  name varchar(50) NOT NULL DEFAULT '' UNIQUE COMMENT '用户名',
  nickname varchar(50) NOT NULL DEFAULT '' COMMENT '昵称',
  gender smallint NOT NULL DEFAULT 0 COMMENT '1-男 2-女 0-保密',
  birthday varchar(50) NOT NULL DEFAULT '' COMMENT '生日 yyyy-mm-dd',
  mobile varchar(50) NOT NULL DEFAULT '' COMMENT '手机号',
  email varchar(50) NOT NULL DEFAULT '' COMMENT '邮箱',
  password varchar(128) NOT NULL DEFAULT '' COMMENT '密码摘要',
  salt varchar(128) NOT NULL DEFAULT '' COMMENT '盐',
  avatar varchar(128) NOT NULL DEFAULT '' COMMENT '头像',
  profile varchar(255) NOT NULL DEFAULT '' COMMENT '个性签名',
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  status int(2) NOT NULL DEFAULT 0 COMMENT '状态: 0-(正常)登录 1-(正常)退出登录 11-(不正常)注销 12-(不正常)封号',
  PRIMARY KEY (uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- mqtt 消息表
DROP TABLE IF EXISTS mqttmessage;

CREATE TABLE mqttmessage(
  id int unsigned NOT NULL auto_increment COMMENT '主键id',
  type smallint NOT NULL DEFAULT 0 COMMENT '消息类型 0-request 1-response 2-relay',
  command varchar(50) NOT NULL COMMENT '命令字',
  msgId varchar(50) COMMENT '消息id uuid',
  payload varchar(5000) NOT NULL COMMENT '消息内容',
  senderId bigint COMMENT '发送者id, 0 - cat   其他 - uid',
  receiverId bigint COMMENT '接收者id, 0 - cat   其他 - uid',
  flagSent smallint NOT NULL DEFAULT 0 COMMENT '已发送标志 0 - 未发送  1 - 已发送',
  sendTime timestamp COMMENT '发送时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='mqtt 消息表';

-- 好友表
DROP TABLE IF EXISTS friend;

CREATE TABLE friend(
  id int unsigned NOT NULL auto_increment COMMENT '主键id',
  uid bigint NOT NULL COMMENT '当前用户 uid',
  friendId bigint NOT NULL COMMENT '好友 uid',
  relation varchar(2000) NOT NULL DEFAULT '' COMMENT '关系',
  state smallint NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-删除/拉黑',
  friendTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '好友时间',
  updateTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  deleteTime timestamp COMMENT '删除/拉黑时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='好友表';

--  追随者表
DROP TABLE IF EXISTS follower;

CREATE TABLE follower(
  id int unsigned NOT NULL auto_increment COMMENT '主键id',
  uid bigint NOT NULL COMMENT '当前用户 uid',
  followerId bigint NOT NULL COMMENT '追随者 uid',
  state smallint NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-删除',
  followTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '追随时间',
  deleteTime timestamp COMMENT '删除时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='追随者表';

-- 群组主表
DROP TABLE IF EXISTS groupmain;

CREATE TABLE groupmain(
    groupId bigint NOT NULL COMMENT '群组ID',
    tag varchar(2000) NOT NULL DEFAULT '' COMMENT '标签',
    name varchar(50) NOT NULL DEFAULT '' UNIQUE COMMENT '群组名',
    avatar varchar(128) NOT NULL DEFAULT '' COMMENT '头像',
    profile varchar(255) NOT NULL DEFAULT '' COMMENT '群组简介',
    createTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    dismissTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '解散时间',
    state smallint NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-解散',
    PRIMARY KEY (groupId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='群组主表';

-- 群组成员表
DROP TABLE IF EXISTS grouper;

CREATE TABLE grouper(
    id int unsigned NOT NULL auto_increment COMMENT '主键id',
    groupId bigint NOT NULL COMMENT '群组ID',
    uid bigint NOT NULL COMMENT '用户 uid',
    role smallint NOT NULL DEFAULT 0 COMMENT '角色 0-群主 1-管理员 9-普通组员',
    joinTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入群时间',
    updateTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    quitTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '退群时间',
    state smallint NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-已退群',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='群组成员表';
