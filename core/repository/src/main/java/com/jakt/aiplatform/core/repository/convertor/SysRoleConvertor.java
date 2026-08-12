package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleDO;
import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.enums.DataScopeEnum;
import com.jakt.aiplatform.core.model.enums.RoleStatusEnum;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;


/**
 * 角色 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysRoleConvertor {

    private SysRoleConvertor() {
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
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
