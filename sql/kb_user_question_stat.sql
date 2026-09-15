-- ------------------------------------------------------------------
-- 表: kb_user_question_stat（用户题目掌握状态 / 错题集）
-- mastered=1 表示做对过 → 组卷默认排除（需求：做对的题不再出）
-- in_wrong_book=1 表示在错题集；错题答对后自动置 0（可手动恢复）
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `kb_user_question_stat` (
  `id`               BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`          BIGINT   NOT NULL COMMENT '用户ID（auth_user.user_id）',
  `question_id`      BIGINT   NOT NULL COMMENT '题目ID（kb_question.id）',
  `right_count`      INT      NOT NULL DEFAULT 0 COMMENT '答对次数',
  `wrong_count`      INT      NOT NULL DEFAULT 0 COMMENT '答错次数',
  `last_result`      TINYINT  DEFAULT NULL COMMENT '最近一次结果（0错/1对）',
  `last_answer_time` DATETIME DEFAULT NULL COMMENT '最近作答时间',
  `first_right_time` DATETIME DEFAULT NULL COMMENT '首次答对时间',
  `mastered`         TINYINT  NOT NULL DEFAULT 0 COMMENT '是否已掌握（答对过=1，组卷排除）',
  `in_wrong_book`    TINYINT  NOT NULL DEFAULT 0 COMMENT '是否在错题集（1在/0已移出）',
  `create_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  KEY `idx_user_mastered` (`user_id`, `mastered`),
  KEY `idx_user_wrong` (`user_id`, `in_wrong_book`, `wrong_count`)
) ENGINE=InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户题目掌握状态（错题集）';
