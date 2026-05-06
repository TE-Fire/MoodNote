-- Active: 1763051471221@@127.0.0.1@3306@moodnote
CREATE DATABASE IF NOT EXISTS moodnote CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE moodnote;

CREATE TABLE IF NOT EXISTS mood_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    gender TINYINT DEFAULT 0 COMMENT '性别：0未知/1男/2女',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '0正常/1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS mood_diary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日记ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '日记标题',
    content TEXT NOT NULL COMMENT '日记内容',
    mood_type TINYINT NOT NULL COMMENT '情绪类型：1开心/2平静/3难过/4焦虑/5生气',
    weather_type TINYINT NOT NULL COMMENT '天气类型：1晴/2多云/3阴/4雨/5雪',
    city VARCHAR(50) DEFAULT NULL COMMENT '城市',
    is_private TINYINT DEFAULT 1 COMMENT '是否私密：0公开/1私密',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '0正常/1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记表';

CREATE TABLE IF NOT EXISTS mood_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(20) NOT NULL COMMENT '标签名称',
    color VARCHAR(7) DEFAULT '#409EFF' COMMENT '标签颜色',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '0正常/1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

CREATE TABLE IF NOT EXISTS mood_diary_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '0正常/1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记-标签关联表';

CREATE INDEX idx_mood_user_username ON mood_user(username);
CREATE INDEX idx_mood_user_email ON mood_user(email);
CREATE INDEX idx_mood_diary_user_id ON mood_diary(user_id);
CREATE INDEX idx_mood_diary_create_time ON mood_diary(create_time);
CREATE INDEX idx_mood_diary_mood_type ON mood_diary(mood_type);
CREATE INDEX idx_mood_tag_user_id ON mood_tag(user_id);
CREATE INDEX idx_mood_diary_tag_diary_id ON mood_diary_tag(diary_id);
CREATE INDEX idx_mood_diary_tag_tag_id ON mood_diary_tag(tag_id);