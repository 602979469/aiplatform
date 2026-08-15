-- ------------------------------------------------------------------
-- 表: auth_login_log（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE `auth_login_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID（失败时可能为空）',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录账号',
  `login_ip` varchar(128) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录IP',
  `user_agent` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '浏览器UA',
  `status` char(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '结果（0成功 1失败 2被踢 3被顶 4注销）',
  `message` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '说明',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_auth_login_log_username` (`username`),
  KEY `idx_auth_login_log_time` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录记录表';
