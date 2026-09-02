-- ============================================================
-- aiplatform 初始化数据（首次部署执行一次）
-- 依赖：先执行 sql/ 下全部建表文件（本文件应最后执行）
-- 全部语句幂等（INSERT IGNORE / ON DUPLICATE KEY UPDATE），可重复执行
-- ============================================================

-- 1. 内置管理员：admin / admin123（仅本地；生产必改）
INSERT INTO auth_user (user_id, username, nickname, password, email, avatar, status, del_flag, remark)
VALUES (1, 'admin', '管理员', '$2y$10$HTZxsxmdoQRWu6O3zeQKnOh5sj6rCO1teMMVLSs7A3.VoENld5LCa',
        'admin@aiplatform.local', '', '0', '0', '内置管理员')
ON DUPLICATE KEY UPDATE
    nickname = VALUES(nickname),
    password = VALUES(password),
    email    = VALUES(email),
    status   = VALUES(status),
    del_flag = VALUES(del_flag);

-- 2. 角色
INSERT INTO auth_role (role_id, role_name, role_key, role_sort, status, del_flag, remark)
VALUES (1, '超级管理员', 'admin',  1, '0', '0', '内置超级管理员'),
       (2, '普通用户',   'common', 2, '0', '0', '注册默认角色')
ON DUPLICATE KEY UPDATE
    role_name = VALUES(role_name),
    role_sort = VALUES(role_sort),
    status    = VALUES(status),
    del_flag  = VALUES(del_flag);

-- 3. 用户-角色
INSERT IGNORE INTO auth_user_role (user_id, role_id) VALUES (1, 1);

-- 4. 菜单（AI 应用 / 系统管理(含文件管理) / 系统监控 / 集群管理）
INSERT INTO auth_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, remark)
VALUES (1,   'AI 应用',    0, 1, '/ai',        '',                            'M', '0', '0', null,                      'el-icon-menu',           'AI 应用目录'),
       (100, 'AI 对话',    1, 1, 'chat',       'ai/chat/index',               'C', '0', '0', 'ai:chat:query',          'el-icon-chat-dot-round',  'AI 对话菜单'),
       (101, '镜像加速器',  1, 2, 'mirror',     'ai/mirror/index',            'C', '0', '0', 'ai:mirror:query',         'el-icon-connection',      '镜像加速器菜单'),
       (200, '系统管理',    0, 2, '/system',    '',                            'M', '0', '0', null,                      'el-icon-setting',         '系统管理目录'),
       (201, '菜单管理',    200, 1, 'menu',     'system/menu/index',           'C', '0', '0', 'auth:menu:list',          'el-icon-menu',            '菜单管理菜单'),
       (202, '用户管理',    200, 2, 'user',     'system/user/index',           'C', '0', '0', 'auth:user:list',          'el-icon-user',            '用户管理菜单'),
       (203, '角色管理',    200, 3, 'role',     'system/role/index',           'C', '0', '0', 'auth:role:list',          'el-icon-user-solid',      '角色管理菜单'),
       (300, '系统监控',    0, 3, '/monitor',   '',                            'M', '0', '0', null,                      'el-icon-monitor',         '系统监控目录'),
       (301, '在线用户',    300, 1, 'online',   'monitor/online/index',        'C', '0', '0', 'auth:online:list',        'el-icon-view',            '在线用户菜单'),
       (302, '登录日志',    300, 2, 'logininfor', 'monitor/logininfor/index',  'C', '0', '0', 'auth:loginlog:list',      'el-icon-document',        '登录日志菜单'),
       (303, '系统日志',    300, 3, 'syslog',   'monitor/syslog/index',        'C', '0', '0', 'monitor:syslog:list',     'el-icon-tickets',         '系统日志菜单'),
       (400, '集群管理',    0, 4, '/cluster',   '',                            'M', '0', '0', null,                      'el-icon-s-data',          '集群管理目录'),
       (401, '数据大盘',    400, 1, 'dashboard', 'cluster/dashboard/index',    'C', '0', '0', 'cluster:dashboard:list',  'el-icon-data-line',       '集群大盘菜单'),
       (405, '镜像管理',    400, 2, 'image',    'cluster/image/index',         'C', '0', '0', 'cluster:image:list',      'el-icon-picture-outline', '集群镜像管理菜单'),
       (402, '配置管理',    400, 3, 'config',   'cluster/config/index',        'C', '0', '0', 'cluster:config:list',     'el-icon-s-tools',         '业务pod配置菜单'),
       (403, '实例管理',    400, 4, 'runtime',  'cluster/runtime/index',       'C', '0', '0', 'cluster:runtime:list',    'el-icon-monitor',         '实例管理菜单'),
       (404, '密钥管理',    400, 5, 'secret',   'cluster/secret/index',        'C', '0', '0', 'cluster:secret:list',     'el-icon-lock',            '集群密钥管理菜单'),
       (500, '文件管理',    200, 4, 'file',     'ParentView',                  'M', '0', '0', null,                      'el-icon-folder',          '文件管理目录（系统管理下）'),
       (501, '文件列表',    500, 1, 'list',     'file/index',                  'C', '0', '0', 'file:list',               'el-icon-folder-opened',   '文件管理列表')
ON DUPLICATE KEY UPDATE
    menu_name = VALUES(menu_name),
    parent_id = VALUES(parent_id),
    order_num = VALUES(order_num),
    path      = VALUES(path),
    component = VALUES(component),
    menu_type = VALUES(menu_type),
    visible   = VALUES(visible),
    status    = VALUES(status),
    perms     = VALUES(perms),
    icon      = VALUES(icon),
    remark    = VALUES(remark);

-- 5. 菜单授权
-- 5.1 超级管理员：全部启用菜单
INSERT IGNORE INTO auth_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM auth_menu WHERE status = '0';

-- 5.2 普通用户：AI 应用（对话/镜像加速）+ 公共功能（系统日志/集群管理/文件管理）
INSERT IGNORE INTO auth_role_menu (role_id, menu_id)
SELECT 2, menu_id FROM auth_menu
WHERE status = '0'
  AND menu_id IN (100, 101, 303, 400, 401, 402, 403, 404, 405, 500, 501);

-- 6. AI 能力（镜像加速器：版本匹配）
INSERT INTO sys_ai_capability
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
