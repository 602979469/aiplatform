package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserRoleDO;
import com.jakt.aiplatform.core.model.domain.SysUserRole;


/**
 * 用户角色关联 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserRoleConvertor {

    private SysUserRoleConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysUserRoleDO 用户角色关联数据对象；为空返回 null
     * @return 用户角色关联领域模型
     */
    public static SysUserRole toModel(SysUserRoleDO source) {
        if (source == null) {
            return null;
        }
        SysUserRole target = new SysUserRole();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setRoleId(source.getRoleId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysUserRole 用户角色关联领域模型
     * @return 用户角色关联数据对象
     */
    public static SysUserRoleDO toDO(SysUserRole source) {
        SysUserRoleDO target = new SysUserRoleDO();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setRoleId(source.getRoleId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
