-- ------------------------------------------------------------------
-- 表: home_purchase_item_image（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE `home_purchase_item_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_id` bigint NOT NULL COMMENT '采购项ID（home_purchase_item.id）',
  `file_id` bigint NOT NULL COMMENT '文件ID（file_info.id）',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '排序（0 起，最多 10 张）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_item` (`item_id`,`order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='家庭装修采购项图片';
