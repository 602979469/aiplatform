-- ------------------------------------------------------------------
-- 表: sys_ai_capability（由 code-generate-template 按 tables 配置生成）
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_ai_capability` (
  `capability_id` bigint NOT NULL AUTO_INCREMENT COMMENT '能力ID',
  `scene_code` varchar(64) NOT NULL COMMENT '场景码',
  `capability_code` varchar(64) NOT NULL COMMENT '能力编码',
  `capability_name` varchar(100) NOT NULL COMMENT '能力名称',
  `description` varchar(500) DEFAULT '' COMMENT '能力描述',
  `skill_rules` text COMMENT '能力约束规则（system提示词）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`capability_id`),
  UNIQUE KEY `uk_scene_cap` (`scene_code`,`capability_code`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI能力';

-- ------------------------------------------------------------------
-- 种子数据：镜像加速器搜索仅使用 IMAGE_VERSION_MATCH，
-- IMAGE_NAME_NORMALIZE / VENDOR_SEARCH_PLAN 未在代码中使用，予以清理。
-- 本段可重复执行（幂等）。
-- ------------------------------------------------------------------
DELETE FROM `sys_ai_capability`
 WHERE `scene_code` = 'MIRROR_ACCELERATOR'
   AND `capability_code` IN ('IMAGE_NAME_NORMALIZE', 'VENDOR_SEARCH_PLAN');

INSERT INTO `sys_ai_capability`
    (`scene_code`, `capability_code`, `capability_name`, `description`,
     `skill_rules`, `status`, `create_by`, `create_time`, `update_time`)
VALUES
    ('MIRROR_ACCELERATOR', 'IMAGE_VERSION_MATCH', '镜像版本匹配',
     '镜像搜索时从厂商 tag 列表选出符合用户期望版本的 tag',
     '你是 Docker 镜像 tag 版本匹配专家。用户会提供：基础镜像名、用户期望版本（如 17、8.0、11）、以及一个厂商镜像的 tag 列表。\n你的任务：从 tag 列表中挑选 1~2 个最符合用户期望版本的 tag。\n硬性规则（违反即拒绝）：\n1. 期望版本不是 latest 时，候选 tag 必须满足以下之一：与期望版本完全相等（如 11 == 11）；或以前缀“期望版本.”开头（如 11 -> 11.0.32、11.0.13-jdk）；或以前缀“期望版本-”开头（如 11 -> 11-jdk、11-jdk-slim）。\n2. 期望版本只是 tag 的子串不算匹配（如 27-ea-11-trixie 对 11 是误判，必须拒绝）。\n3. 期望版本是 latest 时，优先选名为 latest 的 tag；没有则选最新稳定版。\n4. 有 jre/jdk/debian/alpine 等变体时优先主流变体；同版本下优先常规变体。\n只输出严格 JSON（不要输出 JSON 以外的内容）：\n{"matches":[{"tag":"11.0.32-jdk","reason":"主版本 11 前缀匹配","confidence":0.98}],"fallback":""}\n说明：matches 最多 2 个按优先级排序；没有任何合法匹配时 matches 为空数组且 fallback 返回空字符串。',
     '0', 'admin', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    `capability_name` = VALUES(`capability_name`),
    `description`     = VALUES(`description`),
    `skill_rules`     = VALUES(`skill_rules`),
    `status`          = '0',
    `update_time`     = NOW();
