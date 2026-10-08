-- 首页留言板（匿名留言：身份按客户端 IP 归属，同一 IP 同色同头像）
CREATE TABLE IF NOT EXISTS `home_message`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content`        varchar(200) NOT NULL COMMENT '留言内容',
    `client_ip`      varchar(128) NOT NULL DEFAULT '' COMMENT '客户端IP（匿名身份归属 + 审计，不返回前端）',
    `avatar_file_id` bigint                DEFAULT NULL COMMENT '头像文件ID（file_info.id，namespace=avatar）',
    `avatar_name`    varchar(32)  NOT NULL DEFAULT '匿名' COMMENT '匿名昵称（动物名）',
    `color_index`    int          NOT NULL DEFAULT 0 COMMENT '气泡配色下标（同一IP恒定）',
    `create_by`      varchar(64)  NOT NULL DEFAULT '' COMMENT '创建者（登录用户名，仅审计）',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`       char(1)      NOT NULL DEFAULT '0' COMMENT '删除标记（0正常 2删除）',
    PRIMARY KEY (`id`),
    KEY `idx_home_message_time` (`create_time`),
    KEY `idx_home_message_ip` (`client_ip`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='首页留言板留言';
