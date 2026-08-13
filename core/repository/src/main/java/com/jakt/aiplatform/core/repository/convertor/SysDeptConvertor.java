package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysDeptDO;
import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.enums.DeptStatusEnum;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import cn.hutool.core.util.ObjectUtil;

/**
 * 部门 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysDeptConvertor {

    private SysDeptConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param dept 部门领域模型
     * @return 部门查询参数
     */
    public static SysDeptQueryParam toQueryParam(SysDept dept) {
        SysDeptQueryParam query = new SysDeptQueryParam();
        query.setDeptId(dept.getDeptId());
        query.setParentId(dept.getParentId());
        query.setAncestors(dept.getAncestors());
        query.setDeptName(dept.getDeptName());
        query.setOrderNum(dept.getOrderNum());
        query.setLeader(dept.getLeader());
        query.setPhone(dept.getPhone());
        query.setEmail(dept.getEmail());
        query.setStatus(dept.getStatus() == null ? null : dept.getStatus().getCode());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 部门数据对象（条件载体）
     * @return 部门查询参数
     */
    public static SysDeptQueryParam toQueryParam(SysDeptDO condition) {
        SysDeptQueryParam query = new SysDeptQueryParam();
        query.setDeptId(condition.getDeptId());
        query.setParentId(condition.getParentId());
        query.setAncestors(condition.getAncestors());
        query.setDeptName(condition.getDeptName());
        query.setOrderNum(condition.getOrderNum());
        query.setLeader(condition.getLeader());
        query.setPhone(condition.getPhone());
        query.setEmail(condition.getEmail());
        query.setStatus(condition.getStatus());
        return query;
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
        target.setDelFlag(source.getDelFlag());
        target.setParentName(source.getParentName());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
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
        target.setDelFlag(source.getDelFlag());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
