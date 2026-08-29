-- ------------------------------------------------------------------
-- 表: file_info（文件信息表）
-- 一行 = 一个文件；namespace 隔离；storage_name 为系统生成 UUID，不对外暴露；
-- 物理删除（无 del_flag，删除即删 DB 行 + 磁盘文件）
-- ------------------------------------------------------------------
CREATE TABLE `file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `namespace` varchar(64) NOT NULL COMMENT '业务命名空间（磁盘目录名，双重隔离）',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名（含扩展名，展示/下载用）',
  `storage_name` varchar(64) NOT NULL COMMENT '存储文件名（UUID，磁盘实际文件名，不对外暴露）',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(64) NOT NULL DEFAULT '' COMMENT '扩展名（小写，不含点）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_storage_name` (`storage_name`),
  KEY `idx_namespace_id` (`namespace`, `id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件信息表';

insert into auth_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, remark)
values (500, '文件管理', 0, 5, '/file', '', 'M', '0', '0', null, 'el-icon-folder', '文件管理目录'),
       (501, '文件列表', 500, 1, 'list', 'file/index', 'C', '0', '0', 'file:list', 'el-icon-folder-opened', '文件管理列表')
    on duplicate key update
                         menu_name = values(menu_name), parent_id = values(parent_id), order_num = values(order_num),
                         path = values(path), component = values(component), menu_type = values(menu_type),
                         visible = values(visible), status = values(status), perms = values(perms),
                         icon = values(icon), remark = values(remark);

-- 授权给所有启用角色（与 cluster_menu.sql 一致）
insert into auth_role_menu (role_id, menu_id)
select auth_role.role_id, auth_menu.menu_id from auth_role, auth_menu
where auth_role.status = '0' and auth_menu.menu_id in (500, 501)
  and not exists (select 1 from auth_role_menu rm where rm.role_id = auth_role.role_id and rm.menu_id = auth_menu.menu_id);