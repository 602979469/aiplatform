-- ------------------------------------------------------------------
-- 表: kb_exam_template_rule（试卷模板的知识点范围明细）
-- 一个模板多条规则，例如：并发编程/线程池 5 题、JVM/GC 5 题、MySQL 不限子主题 10 题
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `kb_exam_template_rule` (
  `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_id`    BIGINT      NOT NULL COMMENT '模板ID（kb_exam_template.id）',
  `category`       VARCHAR(64) NOT NULL COMMENT '分类（知识点一级，如 并发编程）',
  `subtopic`       VARCHAR(64) DEFAULT NULL COMMENT '子主题（知识点二级，NULL=该分类下全部子主题）',
  `question_type`  VARCHAR(16) DEFAULT NULL COMMENT '限定题型（NULL=不限）',
  `difficulty`     VARCHAR(16) DEFAULT NULL COMMENT '限定难度（NULL=不限）',
  `question_count` INT         NOT NULL COMMENT '该知识点抽题数量',
  `order_num`      INT         NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time`    DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_template` (`template_id`),
  KEY `idx_category` (`category`, `subtopic`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='试卷模板知识点规则';
