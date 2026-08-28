-- ------------------------------------------------------------------
-- 系统日志菜单——挂在「系统监控」(menu_id=300) 下
-- 幂等，可反复执行
-- ------------------------------------------------------------------
insert into auth_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, remark)
values (303, '系统日志', 300, 3, 'syslog', 'monitor/syslog/index', 'C', '0', '0', 'monitor:syslog:list', 'el-icon-tickets', '系统日志菜单')
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

-- 授权给所有启用角色
insert into auth_role_menu (role_id, menu_id)
select auth_role.role_id, auth_menu.menu_id from auth_role, auth_menu
where auth_role.status = '0' and auth_menu.menu_id = 303
  and not exists (select 1 from auth_role_menu rm where rm.role_id = auth_role.role_id and rm.menu_id = auth_menu.menu_id);
