-- ------------------------------------------------------------------
-- 表: sys_ai_session（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE `sys_ai_session` (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `capability_id` bigint NOT NULL COMMENT '能力ID',
  `scene_code` varchar(64) DEFAULT '' COMMENT '场景码',
  `capability_code` varchar(64) DEFAULT '' COMMENT '能力编码',
  `session_name` varchar(100) DEFAULT '系统对话' COMMENT '会话名称',
  `status` char(1) DEFAULT '0' COMMENT '会话状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`session_id`),
  KEY `idx_capability` (`capability_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统AI会话';
