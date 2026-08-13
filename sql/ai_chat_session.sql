-- ------------------------------------------------------------------
-- 表: ai_chat_session（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE `ai_chat_session` (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_name` varchar(100) DEFAULT '新会话' COMMENT '会话名称',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(30) DEFAULT '' COMMENT '用户名',
  `status` char(1) DEFAULT '0' COMMENT '会话状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`session_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户AI会话';
