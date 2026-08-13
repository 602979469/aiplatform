-- ------------------------------------------------------------------
-- 表: sys_ai_capability（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE `sys_ai_capability` (
  `capability_id` bigint NOT NULL AUTO_INCREMENT COMMENT '能力ID',
  `scene_code` varchar(64) NOT NULL COMMENT '场景码',
  `capability_code` varchar(64) NOT NULL COMMENT '能力编码',
  `capability_name` varchar(100) NOT NULL COMMENT '能力名称',
  `description` varchar(500) DEFAULT '' COMMENT '能力描述',
  `skill_rules` text COMMENT '能力约束规则（system提示词）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`capability_id`),
  UNIQUE KEY `uk_scene_cap` (`scene_code`,`capability_code`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI能力';
