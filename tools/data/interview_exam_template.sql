-- 面试题专项试卷模板：20 题 / 40 分钟（每题 120 秒）/ 解答题为主（14 解答 + 4 单选 + 2 判断）
-- 依赖题库：category = '面试题'（20 个方向，各 10 题）
-- 幂等：先按模板名清理同名模板与规则，再重建

SET @tpl_name = '面试题专项 · 20题/40分钟';

DELETE r FROM kb_exam_template_rule r
  JOIN kb_exam_template t ON r.template_id = t.id
 WHERE t.name = @tpl_name;
DELETE FROM kb_exam_template WHERE name = @tpl_name;

INSERT INTO kb_exam_template
  (name, description, scope, owner_user_id, status, mode, question_count, per_question_seconds,
   objective_only, exclude_mastered, type_mix, difficulty_mix, use_count, create_by, update_by,
   create_time, update_time)
VALUES
  (@tpl_name,
   '按简历技术栈生成的面试专项：Java基础/并发/JVM/Spring/MyBatis/MySQL/Redis/MQ/ES/分布式/支付/DDD/K8s/云原生/可观测性/存储/工作流/Netty。180 分钟内完成，解答题为主。',
   'GLOBAL', NULL, 'PUBLISHED', 'NORMAL', 20, 120, 0, 0,
   '{"解答":14,"单选":4,"判断":2}', '{"easy":20,"medium":60,"hard":20}', 0, 'admin', 'admin',
   NOW(), NOW());

SET @tid = LAST_INSERT_ID();

INSERT INTO kb_exam_template_rule
  (template_id, category, subtopic, question_type, difficulty, question_count, order_num, create_time, update_time)
VALUES
  -- 前面 14 个知识点承接 14 道解答题配额（抽题按 order_num 先后分配），故核心栈排在前面
  (@tid, '面试题', 'Java基础',        NULL, NULL, 1, 1,  NOW(), NOW()),
  (@tid, '面试题', '并发编程',        NULL, NULL, 1, 2,  NOW(), NOW()),
  (@tid, '面试题', 'JVM与调优',       NULL, NULL, 1, 3,  NOW(), NOW()),
  (@tid, '面试题', 'Spring',          NULL, NULL, 1, 4,  NOW(), NOW()),
  (@tid, '面试题', 'MySQL',           NULL, NULL, 1, 5,  NOW(), NOW()),
  (@tid, '面试题', 'Redis',           NULL, NULL, 1, 6,  NOW(), NOW()),
  (@tid, '面试题', '分布式与高可用',  NULL, NULL, 1, 7,  NOW(), NOW()),
  (@tid, '面试题', '支付与资金安全',  NULL, NULL, 1, 8,  NOW(), NOW()),
  (@tid, '面试题', 'Docker与K8s',     NULL, NULL, 1, 9,  NOW(), NOW()),
  (@tid, '面试题', '云原生交付',      NULL, NULL, 1, 10, NOW(), NOW()),
  (@tid, '面试题', '工作流Flowable',  NULL, NULL, 1, 11, NOW(), NOW()),
  (@tid, '面试题', 'Netty与长连接',   NULL, NULL, 1, 12, NOW(), NOW()),
  (@tid, '面试题', 'RocketMQ',        NULL, NULL, 1, 13, NOW(), NOW()),
  (@tid, '面试题', 'Elasticsearch',   NULL, NULL, 1, 14, NOW(), NOW()),
  (@tid, '面试题', 'MyBatis',         NULL, NULL, 1, 15, NOW(), NOW()),
  (@tid, '面试题', 'DDD与设计模式',   NULL, NULL, 1, 16, NOW(), NOW()),
  (@tid, '面试题', '可观测性',        NULL, NULL, 1, 17, NOW(), NOW()),
  (@tid, '面试题', '存储与对象存储',  NULL, NULL, 1, 18, NOW(), NOW());

SELECT CONCAT('template_id=', @tid, ' 规则数=', COUNT(*)) AS result
  FROM kb_exam_template_rule WHERE template_id = @tid;
