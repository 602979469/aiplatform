-- ------------------------------------------------------------------
-- 集群管理菜单（主菜单 + 三个子菜单）——幂等，可反复执行
-- 主菜单: 集群管理(M)  子菜单: 数据大盘 / 配置管理 / 实例管理(C)
-- 已存在则更新展示信息，不重复插入；授权按角色去重追加
-- ------------------------------------------------------------------
insert into auth_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, remark)
values (400, '集群管理',   0,    4, '/cluster', '',                        'M', '0', '0', null,                     'el-icon-s-data',      '集群管理目录'),
       (401, '数据大盘',   400,  1, 'dashboard', 'cluster/dashboard/index', 'C', '0', '0', 'cluster:dashboard:list', 'el-icon-data-line',    '集群大盘菜单'),
       (402, '配置管理',   400,  2, 'config',    'cluster/config/index',    'C', '0', '0', 'cluster:config:list',    'el-icon-s-tools',     '业务pod配置菜单'),
       (403, '实例管理',   400,  3, 'runtime',   'cluster/runtime/index',   'C', '0', '0', 'cluster:runtime:list',   'el-icon-monitor',     '实例管理菜单'),
       (404, '密钥管理',   400,  4, 'secret',    'cluster/secret/index',    'C', '0', '0', 'cluster:secret:list',    'el-icon-lock',        '集群密钥管理菜单')
on duplicate key update
    menu_name = values(menu_name),
    parent_id = values(parent_id),
    order_num = values(order_num),
    path = values(path),
    component = values(component),
    menu_type = values(menu_type),
    visible = values(visible),
    status = values(status),
    perms = values(perms),
    icon = values(icon),
    remark = values(remark);

-- 授权给所有启用角色（菜单管理里新增菜单后需重新授权，这里直接全量授权）
insert into auth_role_menu (role_id, menu_id)
select auth_role.role_id, auth_menu.menu_id from auth_role, auth_menu
where auth_role.status = '0' and auth_menu.menu_id in (400, 401, 402, 403, 404)
  and not exists (select 1 from auth_role_menu rm where rm.role_id = auth_role.role_id and rm.menu_id = auth_menu.menu_id);
