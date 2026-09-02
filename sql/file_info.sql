-- ------------------------------------------------------------------
-- 表: file_info（文件信息表）
-- 一行 = 一个文件；namespace 隔离；文件内容存 MinIO 对象存储，DB 只存元数据（object_key 指向对象）；
-- 物理删除（无 del_flag，删除即删 DB 行 + MinIO 对象）
-- ------------------------------------------------------------------
CREATE TABLE `file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `namespace` varchar(64) NOT NULL COMMENT '业务命名空间（隔离维度）',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名（含扩展名，展示/下载用）',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(64) NOT NULL DEFAULT '' COMMENT '扩展名（小写，不含点）',
  `object_key` varchar(255) NOT NULL COMMENT 'MinIO对象键（内容存对象存储，DB只存元数据）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_namespace_id` (`namespace`, `id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件信息表';
