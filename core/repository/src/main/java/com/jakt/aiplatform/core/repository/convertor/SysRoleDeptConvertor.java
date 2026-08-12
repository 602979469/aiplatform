package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleDeptDO;
import com.jakt.aiplatform.core.model.domain.SysRoleDept;


/**
 * 角色部门关联 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysRoleDeptConvertor {

    private SysRoleDeptConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysRoleDeptDO 角色部门关联数据对象；为空返回 null
     * @return 角色部门关联领域模型
     */
    public static SysRoleDept toModel(SysRoleDeptDO source) {
        if (source == null) {
            return null;
        }
        SysRoleDept target = new SysRoleDept();
        target.setId(source.getId());
        target.setRoleId(source.getRoleId());
        target.setDeptId(source.getDeptId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysRoleDept 角色部门关联领域模型
     * @return 角色部门关联数据对象
     */
    public static SysRoleDeptDO toDO(SysRoleDept source) {
        SysRoleDeptDO target = new SysRoleDeptDO();
        target.setId(source.getId());
        target.setRoleId(source.getRoleId());
        target.setDeptId(source.getDeptId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
