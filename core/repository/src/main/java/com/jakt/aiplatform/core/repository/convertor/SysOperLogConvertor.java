package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysOperLogDO;
import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.enums.BusinessStatus;
import com.jakt.aiplatform.core.model.enums.BusinessType;
import com.jakt.aiplatform.core.model.enums.OperatorType;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import cn.hutool.core.util.ObjectUtil;

/**
 * 操作日志 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysOperLogConvertor {

    private SysOperLogConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param operLog 操作日志领域模型
     * @return 操作日志查询参数
     */
    public static SysOperLogQueryParam toQueryParam(SysOperLog operLog) {
        SysOperLogQueryParam query = new SysOperLogQueryParam();
        query.setOperId(operLog.getOperId());
        query.setTitle(operLog.getTitle());
        query.setBusinessType(operLog.getBusinessType() == null ? null : operLog.getBusinessType().getCode());
        query.setMethod(operLog.getMethod());
        query.setRequestMethod(operLog.getRequestMethod());
        query.setOperatorType(operLog.getOperatorType() == null ? null : operLog.getOperatorType().getCode());
        query.setOperName(operLog.getOperName());
        query.setDeptName(operLog.getDeptName());
        query.setOperUrl(operLog.getOperUrl());
        query.setOperIp(operLog.getOperIp());
        query.setOperLocation(operLog.getOperLocation());
        query.setOperParam(operLog.getOperParam());
        query.setJsonResult(operLog.getJsonResult());
        query.setStatus(operLog.getStatus() == null ? null : operLog.getStatus().getCode());
        query.setErrorMsg(operLog.getErrorMsg());
        query.setOperTime(operLog.getOperTime());
        query.setCostTime(operLog.getCostTime());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 操作日志数据对象（条件载体）
     * @return 操作日志查询参数
     */
    public static SysOperLogQueryParam toQueryParam(SysOperLogDO condition) {
        SysOperLogQueryParam query = new SysOperLogQueryParam();
        query.setOperId(condition.getOperId());
        query.setTitle(condition.getTitle());
        query.setBusinessType(condition.getBusinessType());
        query.setMethod(condition.getMethod());
        query.setRequestMethod(condition.getRequestMethod());
        query.setOperatorType(condition.getOperatorType());
        query.setOperName(condition.getOperName());
        query.setDeptName(condition.getDeptName());
        query.setOperUrl(condition.getOperUrl());
        query.setOperIp(condition.getOperIp());
        query.setOperLocation(condition.getOperLocation());
        query.setOperParam(condition.getOperParam());
        query.setJsonResult(condition.getJsonResult());
        query.setStatus(condition.getStatus());
        query.setErrorMsg(condition.getErrorMsg());
        query.setOperTime(condition.getOperTime());
        query.setCostTime(condition.getCostTime());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysOperLogDO 操作日志数据对象；为空返回 null
     * @return 操作日志领域模型
     */
    public static SysOperLog toModel(SysOperLogDO source) {
        if (source == null) {
            return null;
        }
        SysOperLog target = new SysOperLog();
        target.setOperId(source.getOperId());
        target.setTitle(source.getTitle());
        target.setBusinessType(BusinessType.fromCode(source.getBusinessType()));
        target.setMethod(source.getMethod());
        target.setRequestMethod(source.getRequestMethod());
        target.setOperatorType(OperatorType.fromCode(source.getOperatorType()));
        target.setOperName(source.getOperName());
        target.setDeptName(source.getDeptName());
        target.setOperUrl(source.getOperUrl());
        target.setOperIp(source.getOperIp());
        target.setOperLocation(source.getOperLocation());
        target.setOperParam(source.getOperParam());
        target.setJsonResult(source.getJsonResult());
        target.setStatus(BusinessStatus.fromCode(source.getStatus()));
        target.setErrorMsg(source.getErrorMsg());
        target.setOperTime(source.getOperTime());
        target.setCostTime(source.getCostTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysOperLog 操作日志领域模型
     * @return 操作日志数据对象
     */
    public static SysOperLogDO toDO(SysOperLog source) {
        SysOperLogDO target = new SysOperLogDO();
        target.setOperId(source.getOperId());
        target.setTitle(source.getTitle());
        target.setBusinessType(ObjectUtil.isNull(source.getBusinessType()) ? null : source.getBusinessType().getCode());
        target.setMethod(source.getMethod());
        target.setRequestMethod(source.getRequestMethod());
        target.setOperatorType(ObjectUtil.isNull(source.getOperatorType()) ? null : source.getOperatorType().getCode());
        target.setOperName(source.getOperName());
        target.setDeptName(source.getDeptName());
        target.setOperUrl(source.getOperUrl());
        target.setOperIp(source.getOperIp());
        target.setOperLocation(source.getOperLocation());
        target.setOperParam(source.getOperParam());
        target.setJsonResult(source.getJsonResult());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setErrorMsg(source.getErrorMsg());
        target.setOperTime(source.getOperTime());
        target.setCostTime(source.getCostTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
