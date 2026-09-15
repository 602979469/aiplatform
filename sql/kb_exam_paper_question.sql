-- ------------------------------------------------------------------
-- 表: kb_exam_paper_question（试卷题目快照 + 作答）
-- 题目内容/选项/答案/解析在组卷时快照，题库后续变更不影响历史考试回看与判分
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `kb_exam_paper_question` (
  `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `paper_id`            BIGINT       NOT NULL COMMENT '试卷ID（kb_exam_paper.id）',
  `question_id`         BIGINT       NOT NULL COMMENT '来源题目ID（kb_question.id，用于溯源与掌握度统计）',
  `seq`                 INT          NOT NULL COMMENT '题号（从 1 开始）',
  `question_type`       VARCHAR(16)  NOT NULL COMMENT '题型（单选/多选/判断）',
  `category`            VARCHAR(64)  NOT NULL COMMENT '分类',
  `subtopic`            VARCHAR(64)  DEFAULT NULL COMMENT '子主题',
  `title`               VARCHAR(255) NOT NULL COMMENT '题干',
  `content`             MEDIUMTEXT   DEFAULT NULL COMMENT '题目内容（Markdown/HTML）',
  `options`             JSON         DEFAULT NULL COMMENT '选项快照（[{key,text}]）',
  `answer`              VARCHAR(512) DEFAULT NULL COMMENT '正确答案快照（如 A,B,C）',
  `explanation`         TEXT         DEFAULT NULL COMMENT '解析快照',
  `difficulty`          VARCHAR(16)  DEFAULT NULL COMMENT '难度',
  `score`               INT          NOT NULL DEFAULT 5 COMMENT '本题分值',
  `user_answer`         VARCHAR(512) DEFAULT NULL COMMENT '用户作答（如 A,B）',
  `is_correct`          TINYINT      DEFAULT NULL COMMENT '是否正确（NULL未判/0错/1对）',
  `answer_cost_seconds` INT          DEFAULT NULL COMMENT '本题作答耗时（秒，前端上报）',
  `answer_time`         DATETIME     DEFAULT NULL COMMENT '作答时间',
  `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_paper_seq` (`paper_id`, `seq`),
  KEY `idx_question` (`question_id`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='试卷题目快照与作答';
