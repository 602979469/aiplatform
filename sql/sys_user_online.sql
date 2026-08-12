-- ------------------------------------------------------------------
-- 表: sys_user_online（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE `sys_user_online` (
  `sessionId` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户会话id',
  `login_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录账号',
  `dept_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作系统',
  `status` varchar(10) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '在线状态on_line在线off_line离线',
  `start_timestamp` datetime DEFAULT NULL COMMENT 'session创建时间',
  `last_access_time` datetime DEFAULT NULL COMMENT 'session最后访问时间',
  `expire_time` int DEFAULT '0' COMMENT '超时时间，单位为分钟',
  `session_data` blob COMMENT '序列化的Session数据，用于服务重启后恢复会话',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`sessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='在线用户记录';
