-- ------------------------------------------------------------------
-- 表: kb_exam_template（试卷模板：可复用的组卷配置）
-- scope=GLOBAL 管理员发布的公共模板；scope=PERSONAL 用户自己的模板
-- 知识点范围明细见 kb_exam_template_rule
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `kb_exam_template` (
  `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`                 VARCHAR(128) NOT NULL COMMENT '模板名称',
  `description`          VARCHAR(500) DEFAULT NULL COMMENT '模板说明',
  `scope`                VARCHAR(16)  NOT NULL DEFAULT 'PERSONAL' COMMENT '范围（GLOBAL全局/PERSONAL个人）',
  `owner_user_id`        BIGINT       DEFAULT NULL COMMENT '归属用户ID（scope=PERSONAL）',
  `status`               VARCHAR(16)  NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态（DRAFT草稿/PUBLISHED已发布/DISABLED已停用）',
  `mode`                 VARCHAR(16)  NOT NULL DEFAULT 'NORMAL' COMMENT '组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）',
  `question_count`       INT          NOT NULL COMMENT '总题量',
  `per_question_seconds` INT          NOT NULL DEFAULT 60 COMMENT '每题限时（秒）',
  `objective_only`       TINYINT      NOT NULL DEFAULT 1 COMMENT '是否只出客观题（1是/0否，0含解答题走练习模式）',
  `exclude_mastered`     TINYINT      NOT NULL DEFAULT 1 COMMENT '是否排除已做对题目（1是/0否）',
  `type_mix`             JSON         DEFAULT NULL COMMENT '题型配比，如 {"单选":12,"多选":4,"判断":4}',
  `difficulty_mix`       JSON         DEFAULT NULL COMMENT '难度配比，如 {"easy":30,"medium":50,"hard":20}',
  `use_count`            INT          NOT NULL DEFAULT 0 COMMENT '被使用次数',
  `create_by`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag`             CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 2删除）',
  PRIMARY KEY (`id`),
  KEY `idx_scope_owner` (`scope`, `owner_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='试卷模板';
