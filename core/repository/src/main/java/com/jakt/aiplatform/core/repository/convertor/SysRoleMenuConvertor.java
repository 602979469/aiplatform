package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleMenuDO;
import com.jakt.aiplatform.core.model.domain.SysRoleMenu;


/**
 * 角色菜单关联 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysRoleMenuConvertor {

    private SysRoleMenuConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysRoleMenuDO 角色菜单关联数据对象；为空返回 null
     * @return 角色菜单关联领域模型
     */
    public static SysRoleMenu toModel(SysRoleMenuDO source) {
        if (source == null) {
            return null;
        }
        SysRoleMenu target = new SysRoleMenu();
        target.setId(source.getId());
        target.setRoleId(source.getRoleId());
        target.setMenuId(source.getMenuId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysRoleMenu 角色菜单关联领域模型
     * @return 角色菜单关联数据对象
     */
    public static SysRoleMenuDO toDO(SysRoleMenu source) {
        SysRoleMenuDO target = new SysRoleMenuDO();
        target.setId(source.getId());
        target.setRoleId(source.getRoleId());
        target.setMenuId(source.getMenuId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
