-- ============================================================
-- auth 认证与 RBAC 子系统：6 张表 + 种子数据
-- 对齐 code-generate-template 强约束：create_time/update_time 由数据库自动维护
-- ============================================================

-- 1. 用户表
create table if not exists auth_user (
  user_id     bigint       not null auto_increment comment '用户ID',
  username    varchar(50)  not null comment '登录账号',
  nickname    varchar(50)  not null default '' comment '用户昵称',
  password    varchar(100) not null comment '密码（BCrypt 哈希）',
  email       varchar(100) not null default '' comment '邮箱',
  avatar      varchar(255) not null default '' comment '头像路径',
  status      char(1)      not null default '0' comment '状态（0正常 1停用）',
  del_flag    char(1)      not null default '0' comment '删除标志（0存在 2删除）',
  remark      varchar(500) default null comment '备注',
  create_time datetime     not null default current_timestamp comment '创建时间',
  update_time datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (user_id),
  unique key uk_auth_user_username (username)
) engine=innodb auto_increment=100 comment='用户表';

-- 2. 角色表
create table if not exists auth_role (
  role_id     bigint       not null auto_increment comment '角色ID',
  role_name   varchar(50)  not null comment '角色名称',
  role_key    varchar(100) not null comment '角色权限字符串',
  role_sort   int          not null default 0 comment '显示顺序',
  status      char(1)      not null default '0' comment '状态（0正常 1停用）',
  del_flag    char(1)      not null default '0' comment '删除标志（0存在 2删除）',
  remark      varchar(500) default null comment '备注',
  create_time datetime     not null default current_timestamp comment '创建时间',
  update_time datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (role_id),
  unique key uk_auth_role_key (role_key)
) engine=innodb auto_increment=100 comment='角色表';

-- 3. 菜单表（M目录 / C菜单 / F按钮，perms 即权限码来源）
create table if not exists auth_menu (
  menu_id     bigint       not null auto_increment comment '菜单ID',
  menu_name   varchar(50)  not null comment '菜单名称',
  parent_id   bigint       not null default 0 comment '父菜单ID（0 为根）',
  order_num   int          not null default 0 comment '显示顺序',
  path        varchar(200) not null default '' comment '路由地址',
  component   varchar(200) not null default '' comment '组件路径',
  is_frame    char(1)      not null default '1' comment '是否外链（0是 1否）',
  menu_type   char(1)      not null default 'M' comment '类型（M目录 C菜单 F按钮）',
  visible     char(1)      not null default '0' comment '是否显示（0显示 1隐藏）',
  status      char(1)      not null default '0' comment '状态（0正常 1停用）',
  del_flag    char(1)      not null default '0' comment '删除标志（0存在 2删除）',
  perms       varchar(100) default null comment '权限标识',
  icon        varchar(100) not null default '' comment '菜单图标',
  remark      varchar(500) default null comment '备注',
  create_time datetime     not null default current_timestamp comment '创建时间',
  update_time datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (menu_id),
  key idx_auth_menu_parent (parent_id)
) engine=innodb auto_increment=1000 comment='菜单权限表';

-- 4. 用户角色关联表
create table if not exists auth_user_role (
  user_id     bigint   not null comment '用户ID',
  role_id     bigint   not null comment '角色ID',
  create_time datetime not null default current_timestamp comment '创建时间',
  update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (user_id, role_id),
  key idx_auth_user_role_role (role_id)
) engine=innodb comment='用户角色关联表';

-- 5. 角色菜单关联表
create table if not exists auth_role_menu (
  role_id     bigint   not null comment '角色ID',
  menu_id     bigint   not null comment '菜单ID',
  create_time datetime not null default current_timestamp comment '创建时间',
  update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (role_id, menu_id),
  key idx_auth_role_menu_menu (menu_id)
) engine=innodb comment='角色菜单关联表';

-- 6. 登录记录表
create table if not exists auth_login_log (
  log_id     bigint       not null auto_increment comment '日志ID',
  user_id    bigint       default null comment '用户ID（失败时可能为空）',
  username   varchar(50)  not null default '' comment '登录账号',
  login_ip   varchar(128) not null default '' comment '登录IP',
  user_agent varchar(255) not null default '' comment '浏览器UA',
  status     char(1)      not null default '0' comment '结果（0成功 1失败 2被踢 3被顶 4注销）',
  message    varchar(255) not null default '' comment '说明',
  login_time datetime     not null default current_timestamp comment '事件时间',
  create_time datetime    not null default current_timestamp comment '创建时间',
  update_time datetime    not null default current_timestamp on update current_timestamp comment '更新时间',
  primary key (log_id),
  key idx_auth_login_log_username (username),
  key idx_auth_login_log_time (login_time)
) engine=innodb auto_increment=1000 comment='登录记录表';

-- ============================================================
-- 种子数据
-- ============================================================

-- 内置管理员：admin / admin123（仅本地；生产必改）
insert into auth_user (user_id, username, nickname, password, email, avatar, status, del_flag, remark)
values (1, 'admin', '管理员', '$2y$10$HTZxsxmdoQRWu6O3zeQKnOh5sj6rCO1teMMVLSs7A3.VoENld5LCa',
        'admin@aiplatform.local', '', '0', '0', '内置管理员');

insert into auth_role (role_id, role_name, role_key, role_sort, status, del_flag, remark)
values (1, '超级管理员', 'admin',  1, '0', '0', '内置超级管理员'),
       (2, '普通用户',   'common', 2, '0', '0', '注册默认角色');

insert into auth_user_role (user_id, role_id) values (1, 1);

-- 菜单（AI 应用 + 系统管理 + 系统监控）
insert into auth_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, remark)
values (1,   'AI 应用',   0, 1, '/ai',    '',                 'M', '0', '0', null,          'el-icon-menu',          'AI 应用目录'),
       (100, 'AI 对话',   1, 1, 'chat',   'ai/chat/index',    'C', '0', '0', 'ai:chat:query',  'el-icon-chat-dot-round', 'AI 对话菜单'),
       (101, '镜像加速器', 1, 2, 'mirror', 'ai/mirror/index', 'C', '0', '0', 'ai:mirror:query', 'el-icon-connection',    '镜像加速器菜单'),
       (200, '系统管理',   0, 2, '/system', '',                'M', '0', '0', null,             'el-icon-setting',       '系统管理目录'),
       (201, '菜单管理',   200, 1, 'menu', 'system/menu/index', 'C', '0', '0', 'auth:menu:list', 'el-icon-menu',          '菜单管理菜单'),
       (202, '用户管理',   200, 2, 'user', 'system/user/index', 'C', '0', '0', 'auth:user:list', 'el-icon-user',          '用户管理菜单'),
       (203, '角色管理',   200, 3, 'role', 'system/role/index', 'C', '0', '0', 'auth:role:list', 'el-icon-user-solid',    '角色管理菜单'),
       (300, '系统监控',   0, 3, '/monitor', '',                'M', '0', '0', null,             'el-icon-monitor',       '系统监控目录'),
       (301, '在线用户',   300, 1, 'online', 'monitor/online/index', 'C', '0', '0', 'auth:online:list', 'el-icon-view',    '在线用户菜单'),
       (302, '登录日志',   300, 2, 'logininfor', 'monitor/logininfor/index', 'C', '0', '0', 'auth:loginlog:list', 'el-icon-document', '登录日志菜单');

insert into auth_role_menu (role_id, menu_id)
select 1, menu_id from auth_menu where status = '0';
insert into auth_role_menu (role_id, menu_id)
select 2, menu_id from auth_menu where menu_id in (100, 101);
