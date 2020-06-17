
-- 用户表
-- 需添加 性别：gender 1-男 2-女 0-保密
--       简介
DROP TABLE IF EXISTS user;

CREATE TABLE user(
  uid bigint NOT NULL COMMENT '用户ID',
  name varchar(50) NOT NULL DEFAULT '' UNIQUE COMMENT '用户名',
  nickname varchar(50) NOT NULL DEFAULT '' COMMENT '昵称',
  birthday varchar(50) NOT NULL DEFAULT '' COMMENT '生日 yyyy-mm-dd',
  mobile varchar(50) NOT NULL DEFAULT '' COMMENT '手机号',
  email varchar(50) NOT NULL DEFAULT '' COMMENT '邮箱',
  password varchar(128) NOT NULL DEFAULT '' COMMENT '密码摘要',
  salt varchar(128) NOT NULL DEFAULT '' COMMENT '盐',
  avatar varchar(128) NOT NULL DEFAULT '' COMMENT '头像',
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  status int(2) NOT NULL DEFAULT 0 COMMENT '状态: 0-(正常)在线 1-(正常)不在线 11-(不正常)注销 12-(不正常)封号',
  PRIMARY KEY (uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
