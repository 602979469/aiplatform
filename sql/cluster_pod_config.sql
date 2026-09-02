-- ------------------------------------------------------------------
-- 表: cluster_pod_config（业务 pod 配置表）
-- 一行 = 一个业务 pod（pod_name 唯一）；status 为配置状态机
-- ------------------------------------------------------------------
CREATE TABLE `cluster_pod_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_name` varchar(64) NOT NULL COMMENT '资源名称（中文名）',
  `pod_name` varchar(64) NOT NULL COMMENT '业务pod名称（镜像名）',
  `namespace` varchar(64) NOT NULL COMMENT '业务命名空间',
  `deploy_yaml` mediumtext NOT NULL COMMENT 'Deployment YAML',
  `last_built_commit` varchar(64) DEFAULT NULL COMMENT '上次构建commit',
  `status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态（DRAFT草稿/BUILDING构建中/BUILD_FAILED构建失败/PUBLISHED发布/RETIRED弃用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pod_name` (`pod_name`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务pod配置表';
