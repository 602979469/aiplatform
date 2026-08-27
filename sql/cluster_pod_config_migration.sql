-- ------------------------------------------------------------------
-- 迁移：cluster_pod_config 去版本号/逻辑删除，加状态机，pod_name 唯一
-- 依据决策：旧多版本数据清除，仅保留每个 pod_name 最新一条有效配置
-- ------------------------------------------------------------------

-- 1. 清除旧数据：只保留每个 pod_name 最新一条（del_flag=0），其余删除
DELETE t1 FROM cluster_pod_config t1
JOIN cluster_pod_config t2
  ON t1.pod_name = t2.pod_name
 AND t1.id < t2.id
WHERE t2.del_flag = '0';

-- 清理已删除（del_flag=2）的残留
DELETE FROM cluster_pod_config WHERE del_flag = '2';

-- 2. 删除旧唯一约束与版本列
ALTER TABLE cluster_pod_config DROP INDEX uk_pod_version;
ALTER TABLE cluster_pod_config DROP COLUMN version_no;
ALTER TABLE cluster_pod_config DROP COLUMN del_flag;

-- 3. 加状态列（默认草稿）
ALTER TABLE cluster_pod_config ADD COLUMN status varchar(20) NOT NULL DEFAULT 'DRAFT'
  COMMENT '状态（DRAFT草稿/BUILDING构建中/BUILD_FAILED构建失败/PUBLISHED发布/RETIRED弃用）' AFTER last_built_commit;

-- 4. pod_name 唯一
ALTER TABLE cluster_pod_config ADD UNIQUE KEY uk_pod_name (pod_name);
