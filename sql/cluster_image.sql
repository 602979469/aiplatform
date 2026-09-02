-- ------------------------------------------------------------------
-- 表: cluster_image（镜像表：一个镜像名多个版本）
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cluster_image` (
  `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `image_name`        VARCHAR(128) NOT NULL COMMENT '标准化镜像名（小写字母/数字/下划线）',
  `version`           VARCHAR(128) NOT NULL COMMENT '版本/tag，如 8.0 / v1.0.0',
  `image_type`        VARCHAR(20)  NOT NULL COMMENT '来源类型（BUILD 自研 / EXTERNAL 现成）',
  `git_url`           VARCHAR(512) DEFAULT NULL COMMENT 'git地址（imageType=BUILD）',
  `git_branch`        VARCHAR(128) DEFAULT NULL COMMENT 'git分支（imageType=BUILD）',
  `dockerfile`        TEXT         DEFAULT NULL COMMENT 'Dockerfile内容（imageType=BUILD）',
  `external_image`    VARCHAR(512) DEFAULT NULL COMMENT '外部镜像地址（imageType=EXTERNAL）',
  `harbor_ref`        VARCHAR(512) DEFAULT NULL COMMENT 'Harbor完整引用，如 harbor.jakt.online/library/xxx:tag',
  `tar_name`          VARCHAR(255) DEFAULT NULL COMMENT 'tar归档名，如 xxx_tag.tar.gz（MinIO image-tars/）',
  `build_status`      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态（DRAFT草稿/BUILDING构建中/BUILD_FAILED构建失败/PUBLISHED已发布）',
  `build_retry_count` INT          NOT NULL DEFAULT 0 COMMENT '构建失败已重试次数（≤3）',
  `build_log_path`    VARCHAR(512) DEFAULT NULL COMMENT '构建日志路径',
  `create_by`         VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`         VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`            VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_image_version` (`image_name`, `version`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='镜像表';
