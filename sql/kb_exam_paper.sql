-- ------------------------------------------------------------------
-- 表: kb_exam_paper（考试试卷：一次考试 = 一张卷）
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `kb_exam_paper` (
  `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`              BIGINT       NOT NULL COMMENT '答题用户ID（auth_user.user_id）',
  `title`                VARCHAR(128) NOT NULL DEFAULT '模拟考试' COMMENT '试卷标题',
  `mode`                 VARCHAR(16)  NOT NULL DEFAULT 'NORMAL' COMMENT '组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）',
  `status`               VARCHAR(16)  NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态（IN_PROGRESS进行中/GRADED已判分/ABANDONED已放弃）',
  `per_question_seconds` INT          NOT NULL DEFAULT 60 COMMENT '每题限时（秒）',
  `time_limit_seconds`   INT          NOT NULL COMMENT '整卷限时（秒）= 题目数 × 每题限时',
  `question_count`       INT          NOT NULL COMMENT '题目数量',
  `total_score`          INT          NOT NULL DEFAULT 0 COMMENT '试卷总分',
  `score`                INT          DEFAULT NULL COMMENT '得分',
  `correct_count`        INT          DEFAULT NULL COMMENT '答对题数',
  `wrong_count`          INT          DEFAULT NULL COMMENT '答错题数',
  `unanswered_count`     INT          DEFAULT NULL COMMENT '未作答题数',
  `categories`           JSON         DEFAULT NULL COMMENT '组卷分类与题量快照（[{category,count}]）',
  `start_time`           DATETIME     NOT NULL COMMENT '开始时间',
  `deadline`             DATETIME     NOT NULL COMMENT '截止时间（开始时间 + 整卷限时）',
  `submit_time`          DATETIME     DEFAULT NULL COMMENT '交卷时间',
  `cost_seconds`         INT          DEFAULT NULL COMMENT '实际用时（秒）',
  `create_by`            VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`            VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`               VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试试卷';
