package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserRoleDO;
import com.jakt.aiplatform.core.model.domain.SysUserRole;
import com.jakt.aiplatform.core.model.param.SysUserRoleQueryParam;

/**
 * 用户角色关联 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserRoleConvertor {

    private SysUserRoleConvertor() {
    }

    /**
     * 领域模型 → 查询参数（显式赋值）。
     *
     * @param userRole 用户角色关联领域模型
     * @return 用户角色关联查询参数
     */
    public static SysUserRoleQueryParam toQueryParam(SysUserRole userRole) {
        SysUserRoleQueryParam query = new SysUserRoleQueryParam();
        query.setId(userRole.getId());
        query.setUserId(userRole.getUserId());
        query.setRoleId(userRole.getRoleId());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 用户角色关联数据对象（条件载体）
     * @return 用户角色关联查询参数
     */
    public static SysUserRoleQueryParam toQueryParam(SysUserRoleDO condition) {
        SysUserRoleQueryParam query = new SysUserRoleQueryParam();
        query.setId(condition.getId());
        query.setUserId(condition.getUserId());
        query.setRoleId(condition.getRoleId());
        return query;
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
