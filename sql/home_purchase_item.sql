-- ------------------------------------------------------------------
-- 表: home_purchase_item（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE `home_purchase_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `big_type_code` varchar(64) NOT NULL COMMENT '大类编码（如 hardcover）',
  `big_type_name` varchar(64) NOT NULL COMMENT '大类名称（如 硬装）',
  `type_code` varchar(64) NOT NULL COMMENT '小类编码（如 air_conditioner）',
  `type_name` varchar(64) NOT NULL COMMENT '小类名称（如 空调）',
  `product_name` varchar(200) NOT NULL COMMENT '产品名称',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '采购数量',
  `budget_text` varchar(64) NOT NULL COMMENT '预算原文（800~1200 或 999）',
  `budget_min` decimal(12,2) DEFAULT NULL COMMENT '预算下限（单件）',
  `budget_max` decimal(12,2) DEFAULT NULL COMMENT '预算上限（单件）',
  `install_fee` decimal(12,2) DEFAULT NULL COMMENT '安装费（单件）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `user_id` bigint DEFAULT NULL COMMENT '录入人（auth_user.user_id）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记（0正常 2删除）',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`big_type_code`,`type_code`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='家庭装修采购项';
