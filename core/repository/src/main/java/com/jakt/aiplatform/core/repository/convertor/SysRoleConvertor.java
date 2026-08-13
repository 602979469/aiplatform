package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleDO;
import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.enums.DataScopeEnum;
import com.jakt.aiplatform.core.model.enums.RoleStatusEnum;
import com.jakt.aiplatform.core.model.param.SysRoleQueryParam;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;


/**
 * 角色 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysRoleConvertor {

    private SysRoleConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code、String 排序转 Integer，显式赋值）。
     *
     * @param role 角色领域模型
     * @return 角色查询参数
     */
    public static SysRoleQueryParam toQueryParam(SysRole role) {
        SysRoleQueryParam query = new SysRoleQueryParam();
        query.setRoleId(role.getRoleId());
        query.setRoleName(role.getRoleName());
        query.setRoleKey(role.getRoleKey());
        query.setRoleSort(StrUtil.isBlank(role.getRoleSort()) ? null : Convert.toInt(role.getRoleSort()));
        query.setDataScope(role.getDataScope() == null ? null : role.getDataScope().getCode());
        query.setStatus(role.getStatus() == null ? null : role.getStatus().getCode());
        query.setRemark(role.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 角色数据对象（条件载体）
     * @return 角色查询参数
     */
    public static SysRoleQueryParam toQueryParam(SysRoleDO condition) {
        SysRoleQueryParam query = new SysRoleQueryParam();
        query.setRoleId(condition.getRoleId());
        query.setRoleName(condition.getRoleName());
        query.setRoleKey(condition.getRoleKey());
        query.setRoleSort(condition.getRoleSort());
        query.setDataScope(condition.getDataScope());
        query.setStatus(condition.getStatus());
        query.setRemark(condition.getRemark());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysRoleDO 角色数据对象；为空返回 null
     * @return 角色领域模型
     */
    public static SysRole toModel(SysRoleDO source) {
        if (source == null) {
            return null;
        }
        SysRole target = new SysRole();
        target.setRoleId(source.getRoleId());
        target.setRoleName(source.getRoleName());
        target.setRoleKey(source.getRoleKey());
        target.setRoleSort(Convert.toStr(source.getRoleSort()));
        target.setDataScope(DataScopeEnum.fromCode(source.getDataScope()));
        target.setStatus(RoleStatusEnum.fromCode(source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysRole 角色领域模型
     * @return 角色数据对象
     */
    public static SysRoleDO toDO(SysRole source) {
        SysRoleDO target = new SysRoleDO();
        target.setRoleId(source.getRoleId());
        target.setRoleName(source.getRoleName());
        target.setRoleKey(source.getRoleKey());
        target.setRoleSort(Convert.toInt(source.getRoleSort()));
        target.setDataScope(ObjectUtil.isNull(source.getDataScope()) ? null : source.getDataScope().getCode());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
