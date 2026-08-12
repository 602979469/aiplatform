package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysDeptDO;
import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.enums.DeptStatusEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 部门 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysDeptConvertor {

    private SysDeptConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysDeptDO 部门数据对象；为空返回 null
     * @return 部门领域模型
     */
    public static SysDept toModel(SysDeptDO source) {
        if (source == null) {
            return null;
        }
        SysDept target = new SysDept();
        target.setDeptId(source.getDeptId());
        target.setParentId(source.getParentId());
        target.setAncestors(source.getAncestors());
        target.setDeptName(source.getDeptName());
        target.setOrderNum(source.getOrderNum());
        target.setLeader(source.getLeader());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setStatus(DeptStatusEnum.fromCode(source.getStatus()));
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysDept 部门领域模型
     * @return 部门数据对象
     */
    public static SysDeptDO toDO(SysDept source) {
        SysDeptDO target = new SysDeptDO();
        target.setDeptId(source.getDeptId());
        target.setParentId(source.getParentId());
        target.setAncestors(source.getAncestors());
        target.setDeptName(source.getDeptName());
        target.setOrderNum(source.getOrderNum());
        target.setLeader(source.getLeader());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
