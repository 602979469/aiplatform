# 考题系统设计（基于 kb_question 题库）

> 现状：`kb_question` 共 4390 题（单选 2612 / 多选 877 / 判断 901），29 个分类、186 个子主题；
> 检索走 Elasticsearch 索引 `java-kb`（`KbQuestionController#search`），详情目前查 MySQL。

## 1. 总体分工：MySQL 管状态，ES 管搜索

| 数据 | 存储 | 原因 |
| --- | --- | --- |
| 题库内容 `kb_question` | MySQL（主）+ ES `java-kb`（检索副本） | 搜索/关键词召回用 ES |
| 试卷、作答、判分、错题、掌握度 | **MySQL** | 需要事务、实时、强一致 |

关键决策：**"做过的题不再出"这类用户态过滤必须在 MySQL 做**，不能塞进 ES。
否则每个用户的答题状态都要同步进 ES，多一份同步成本与不一致风险。

需要"按关键词搜题再组卷"时：先用 ES 召回候选 id，再交给 MySQL 组卷逻辑（排除已掌握）。

## 2. 数据模型

### 2.1 沿用：`kb_question`

```
id, question_type(单选/多选/判断), category, subtopic, title, content,
options(json [{key,text}]), answer("A,B,C,E"), explanation, difficulty, tags, source_path
```

### 2.2 新增：试卷（一次考试 = 一张卷）

```sql
CREATE TABLE kb_exam_paper (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL COMMENT '答题用户（auth_user.user_id）',
  title varchar(128) NOT NULL DEFAULT '模拟考试',
  mode varchar(16) NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL 新题 / WRONG_BOOK 错题重练 / REVIEW 复习（含已掌握）',
  status varchar(16) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT 'IN_PROGRESS/GRADED/ABANDONED',
  per_question_seconds int NOT NULL DEFAULT 60 COMMENT '每题秒数',
  time_limit_seconds int NOT NULL COMMENT '整卷时限 = 题数 × 每题秒数',
  question_count int NOT NULL,
  total_score int NOT NULL DEFAULT 0,
  score int DEFAULT NULL,
  correct_count int DEFAULT NULL,
  wrong_count int DEFAULT NULL,
  unanswered_count int DEFAULT NULL,
  categories json DEFAULT NULL COMMENT '组卷时的分类/题量快照',
  start_time datetime NOT NULL,
  deadline datetime NOT NULL,
  submit_time datetime DEFAULT NULL,
  cost_seconds int DEFAULT NULL,
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_time (user_id, create_time),
  KEY idx_status (status)
) COMMENT '考试试卷';
```

### 2.3 新增：试卷题目快照 + 作答

题目内容、选项、答案、解析都在组卷时**快照**进来 —— 题库以后改了，历史考试仍能原样回看和判分。

```sql
CREATE TABLE kb_exam_paper_question (
  id bigint NOT NULL AUTO_INCREMENT,
  paper_id bigint NOT NULL,
  question_id bigint NOT NULL COMMENT '来源题目 id（保留溯源）',
  seq int NOT NULL COMMENT '题号，从 1 开始',
  question_type varchar(16) NOT NULL,
  category varchar(64) NOT NULL,
  subtopic varchar(64) DEFAULT NULL,
  title varchar(255) NOT NULL,
  content mediumtext,
  options json COMMENT '选项快照',
  answer varchar(512) COMMENT '正确答案快照',
  explanation text COMMENT '解析快照',
  difficulty varchar(16) DEFAULT NULL,
  score int NOT NULL DEFAULT 5,
  user_answer varchar(512) DEFAULT NULL,
  is_correct tinyint DEFAULT NULL COMMENT 'NULL 未判 / 0 错 / 1 对',
  answer_cost_seconds int DEFAULT NULL,
  answer_time datetime DEFAULT NULL,
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_paper_seq (paper_id, seq),
  KEY idx_question (question_id)
) COMMENT '试卷题目快照与作答';
```

### 2.4 新增：用户题目掌握状态（需求 1「错题集」+ 需求 4「做对不再出」的核心）

```sql
CREATE TABLE kb_user_question_stat (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  question_id bigint NOT NULL,
  right_count int NOT NULL DEFAULT 0,
  wrong_count int NOT NULL DEFAULT 0,
  last_result tinyint DEFAULT NULL COMMENT '最近一次 0 错 1 对',
  last_answer_time datetime DEFAULT NULL,
  first_right_time datetime DEFAULT NULL,
  mastered tinyint NOT NULL DEFAULT 0 COMMENT '答对过=已掌握 → 组卷默认排除',
  in_wrong_book tinyint NOT NULL DEFAULT 0 COMMENT '是否在错题集（可手动移出）',
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_question (user_id, question_id),
  KEY idx_user_mastered (user_id, mastered),
  KEY idx_user_wrong (user_id, in_wrong_book, wrong_count)
) COMMENT '用户题目掌握状态/错题集';
```

**错题集不单独建表**，由本表派生（`wrong_count > 0 AND in_wrong_book = 1`），避免两表同步不一致。

## 3. 组卷算法（需求 2 / 4）

输入：`categories[]`（可多选）+ 题量 + 题型配比（可选）+ 难度（可选）+ 模式。

```
1. 排除集：SELECT question_id FROM kb_user_question_stat
          WHERE user_id=? AND mastered=1            -- 需求4：做对过的题不再出
          （REVIEW 复习模式跳过此步；WRONG_BOOK 模式改为只取错题集）
2. 题量分配：总题数按分类均分，余数补到前面的分类；也可由前端逐类指定题量
3. 抽题：SELECT id FROM kb_question
        WHERE category=? AND question_type IN (?)
          AND id NOT IN (排除集)
        ORDER BY RAND() LIMIT n
   （4390 题规模 ORDER BY RAND() 完全够用；>10 万题再换随机 id 采样或 ES random_score）
4. 兜底：某分类可用题量不足 → 从同 subtopic / 相邻分类补；仍不足则截断并提示
5. 落库：事务内写 kb_exam_paper + kb_exam_paper_question（快照），状态 IN_PROGRESS
6. 返回给前端：题目 + 选项，**不含 answer / explanation**
```

并发保护：同一用户同时只允许一张 `IN_PROGRESS` 的卷（查询校验 + 继续上次考试）。

## 4. 考试流程与计时（需求 3 / 6）

状态机：`IN_PROGRESS → GRADED`（交卷时判分）；超时未交 → 自动交卷判分；中途放弃 → `ABANDONED`（按已答判分）。

**计时（推荐方案）**

- 前端：每题独立 60s 环形倒计时，超时自动提交本题并跳下一题（需求 6 的体验）。
- 后端：整卷 `deadline = start_time + question_count × per_question_seconds`，交卷时校验（允许 5s 网络容差），防止前端改时间。
- 每题耗时由前端上报（`answer_cost_seconds`），主要用于统计，不参与判分。

**答案落库时机**：每题提交即写 `kb_exam_paper_question.user_answer`（`PUT .../answer`，按 seq 幂等覆盖）。
好处：刷新/断网/换设备都能续考，不会因为没交卷丢答案。

**判分规则**

| 题型 | 规则 |
| --- | --- |
| 单选 / 判断 | 完全匹配 |
| 多选 | 完全一致才算对（先不做部分分，可配置） |
| 未作答 | 记 `is_correct=0` 且计入 `unanswered_count` |

交卷事务内：更新 paper 汇总 → 回填每题 `is_correct` → upsert `kb_user_question_stat`：

- 答对：`right_count++`、`last_result=1`、`mastered=1`、`first_right_time` 首次写入
- 答错：`wrong_count++`、`last_result=0`、`in_wrong_book=1`
- 可配置：错题答对一次即自动移出错题集（`in_wrong_book=0`）

## 5. 接口设计

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/kb/exam/categories` | 可选分类/子主题 + 该用户可用题量（已扣掉做对过的） |
| POST | `/api/kb/exam/paper` | 开始考试（分类/题量/题型/模式）→ 返回卷子（无答案） |
| GET | `/api/kb/exam/paper/{id}` | 续考：卷子 + 已答 + 剩余时间 |
| PUT | `/api/kb/exam/paper/{id}/answer` | 提交单题（幂等） |
| POST | `/api/kb/exam/paper/{id}/submit` | 交卷判分 → 成绩 |
| GET | `/api/kb/exam/paper/{id}/result` | 成绩详情（含错题与解析） |
| GET | `/api/kb/exam/history` | 考试记录列表（分页） |
| GET | `/api/kb/exam/wrong` | 错题集（分类/错次/时间筛选） |
| POST | `/api/kb/exam/wrong/{questionId}/mastered` | 手动标记已掌握（移出错题集） |
| GET | `/api/kb/exam/stats` | 个人统计：正确率趋势、分类强弱项 |

## 6. 前端设计（需求 5）

沿用现有 Vue2 + Element UI（RuoYi 风格），新增 5 个页面：

1. **组卷页**：分类卡片多选（卡片上显示"可用 312 / 共 350 题"，实时反映做对过的题被排除后还剩多少）、题量滑块、题型配比、模式切换（新题 / 错题重练 / 复习）、"预计 X 分钟"提示。
2. **答题页**（核心）：
   - 单题一屏：题干 + 整块可点的选项卡片
   - 顶部环形倒计时（60s，最后 10s 变红）
   - 答题卡：题号网格（已答 / 未答 / 标记 / 当前），点击跳题
   - 快捷键：A–E 选项、Enter 下一题、F 标记
   - 交卷确认弹窗（提示未答数量）；断线/刷新自动恢复
3. **成绩页**：总分环形图、正确率、用时、分类正确率柱状图、错题列表（可展开解析）、"错题重练"按钮。
4. **错题集页**：题干摘要 + 分类 + 错次 + 上次错误时间 + 我的答案 vs 正确答案 + 解析；支持筛选、批量重练、标记已掌握。
5. **历史记录页**：列表 + 点击进入当次成绩详情。

交互参考：牛客网（答题卡 + 进度 + 倒计时）、考试星/问卷星（组卷配置）、LeetCode（题目卡片）。

## 7. 实施分期

| 阶段 | 内容 | 预估 |
| --- | --- | --- |
| P0 | 建表 + 组卷 + 答题 + 判分 + 考试记录（闭环可用） | 1–2 天 |
| P1 | 错题集 + 掌握度排除（需求 1、4）+ 断线续考 | 1 天 |
| P2 | 成绩分析/统计 + UI 打磨（动画、动效、移动端适配） | 1–2 天 |
| P3 | ES 搜索组卷、AI 解析/相似题推荐（复用现有 DeepSeek key） | 按需 |

## 8. 现有代码需要先修的两处（否则考试系统会踩坑）

1. **题目详情接口目前 500**：`KbQuestionMapper.xml` / `KbQuestionDO` 仍在读写 `doc_type` 字段，
   但线上表已经改成 `question_type` + `subtopic`（实测 `GET /api/kb/question/3617` 返回 `SYSTEM_ERROR`）。
   考试系统要用题干/选项/答案/解析，这块必须先对齐。
2. **ES 与 MySQL 的同步机制不在代码里**：仓库中没有任何写入 `java-kb` 的代码（只有查询），
   说明索引是外部灌入的。题库新增/修改后 ES 不会自动更新，需要补同步任务（应用内定时增量 或 outbox 事件）。
