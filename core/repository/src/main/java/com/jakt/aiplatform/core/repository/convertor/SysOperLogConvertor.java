package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysOperLogDO;
import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.enums.BusinessTypeEnum;
import com.jakt.aiplatform.core.model.enums.OperatorTypeEnum;
import com.jakt.aiplatform.core.model.enums.BusinessStatusEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 操作日志 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysOperLogConvertor {

    private SysOperLogConvertor() {
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
        target.setBusinessType(BusinessTypeEnum.fromCode(source.getBusinessType()));
        target.setMethod(source.getMethod());
        target.setRequestMethod(source.getRequestMethod());
        target.setOperatorType(OperatorTypeEnum.fromCode(source.getOperatorType()));
        target.setOperName(source.getOperName());
        target.setDeptName(source.getDeptName());
        target.setOperUrl(source.getOperUrl());
        target.setOperIp(source.getOperIp());
        target.setOperLocation(source.getOperLocation());
        target.setOperParam(source.getOperParam());
        target.setJsonResult(source.getJsonResult());
        target.setStatus(BusinessStatusEnum.fromCode(source.getStatus()));
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
