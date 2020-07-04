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
  status int(2) NOT NULL DEFAULT 0 COMMENT '状态: 0-(正常)在线 1-(正常)不在线 11-(不正常)注销 12-(不正常)封号',
  PRIMARY KEY (uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 消息表
DROP TABLE IF EXISTS message;

CREATE TABLE message(
  id bigint NOT NULL COMMENT '主键id',
  type varchar(50) NOT NULL DEFAULT '' COMMENT '消息类型',
  toUid bigint NOT NULL DEFAULT 0 COMMENT '目标用户ID',
  content varchar(5000) NOT NULL DEFAULT '' COMMENT '消息内容',
  createTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  isSended smallint NOT NULL DEFAULT 0 COMMENT '发送标志 0 - 未发送 1 - 已发送',
  sendTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';
