-- 创建数据库
create schema if not exists user_center;
-- 切换数据库
use user_center;
-- 用户表信息
create table if not exists user_center.`user`
(
    `id`            bigint                                 not null auto_increment comment '主键' primary key,
    `username`      varchar(256)                           null comment '用户名',
    `user_account`  varchar(256)                           not null comment '登录账号',
    `user_password` varchar(256)                           not null comment '密码',
    `avatar_url`    varchar(512)                           null comment '头像',
    `gender`        tinyint                                null comment '性别',
    `phone`         varchar(256)                           null comment '手机号',
    `email`         varchar(512)                           null comment '邮箱',
    `user_status`   varchar(256) default '0'               not null comment '用户状态 0 - 正常',
    `user_role`     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    `update_time`   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `create_time`   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    `is_deleted`    tinyint      default 0                 not null comment '是否删除(0-未删, 1-已删)'

) comment '用户' collate = utf8mb4_unicode_ci;