package com.jakt.aiplatform.core.repository.convertor;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthRoleDO;
import com.jakt.aiplatform.common.dal.query.AuthRoleDalQuery;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;


/**
 * 角色表 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AuthRoleConvertor {

    private AuthRoleConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param authRoleDO 角色表数据对象；为空返回 null
     * @return 角色表领域模型
     */
    public static AuthRole toModel(AuthRoleDO source) {
        if (source == null) {
            return null;
        }
        AuthRole target = new AuthRole();
        target.setRoleId(source.getRoleId());
        target.setRoleName(source.getRoleName());
        target.setRoleKey(source.getRoleKey());
        target.setRoleSort(source.getRoleSort());
        target.setStatus(BaseEnum.fromCode(EnableStatusEnum.class, source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param authRole 角色表领域模型
     * @return 角色表数据对象
     */
    public static AuthRoleDO toDO(AuthRole source) {
        if (source == null) {
            return null;
        }
        AuthRoleDO target = new AuthRoleDO();
        target.setRoleId(source.getRoleId());
        target.setRoleName(source.getRoleName());
        target.setRoleKey(source.getRoleKey());
        target.setRoleSort(source.getRoleSort());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    public static AuthRoleDalQuery toDalQuery(AuthRoleQueryParam source) {
        AuthRoleDalQuery target = new AuthRoleDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setRoleId(source.getRoleId());
        target.setRoleName(source.getRoleName());
        target.setRoleKey(source.getRoleKey());
        target.setRoleSort(source.getRoleSort());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
