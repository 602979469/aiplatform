package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysLogininforDO;
import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.model.enums.LoginStatusEnum;
import com.jakt.aiplatform.core.model.param.SysLogininforQueryParam;
import cn.hutool.core.util.ObjectUtil;

/**
 * 登录日志 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysLogininforConvertor {

    private SysLogininforConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param logininfor 登录日志领域模型
     * @return 登录日志查询参数
     */
    public static SysLogininforQueryParam toQueryParam(SysLogininfor logininfor) {
        SysLogininforQueryParam query = new SysLogininforQueryParam();
        query.setInfoId(logininfor.getInfoId());
        query.setLoginName(logininfor.getLoginName());
        query.setIpaddr(logininfor.getIpaddr());
        query.setLoginLocation(logininfor.getLoginLocation());
        query.setBrowser(logininfor.getBrowser());
        query.setOs(logininfor.getOs());
        query.setStatus(logininfor.getStatus() == null ? null : logininfor.getStatus().getCode());
        query.setMsg(logininfor.getMsg());
        query.setLoginTime(logininfor.getLoginTime());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 登录日志数据对象（条件载体）
     * @return 登录日志查询参数
     */
    public static SysLogininforQueryParam toQueryParam(SysLogininforDO condition) {
        SysLogininforQueryParam query = new SysLogininforQueryParam();
        query.setInfoId(condition.getInfoId());
        query.setLoginName(condition.getLoginName());
        query.setIpaddr(condition.getIpaddr());
        query.setLoginLocation(condition.getLoginLocation());
        query.setBrowser(condition.getBrowser());
        query.setOs(condition.getOs());
        query.setStatus(condition.getStatus());
        query.setMsg(condition.getMsg());
        query.setLoginTime(condition.getLoginTime());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysLogininforDO 登录日志数据对象；为空返回 null
     * @return 登录日志领域模型
     */
    public static SysLogininfor toModel(SysLogininforDO source) {
        if (source == null) {
            return null;
        }
        SysLogininfor target = new SysLogininfor();
        target.setInfoId(source.getInfoId());
        target.setLoginName(source.getLoginName());
        target.setIpaddr(source.getIpaddr());
        target.setLoginLocation(source.getLoginLocation());
        target.setBrowser(source.getBrowser());
        target.setOs(source.getOs());
        target.setStatus(LoginStatusEnum.fromCode(source.getStatus()));
        target.setMsg(source.getMsg());
        target.setLoginTime(source.getLoginTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysLogininfor 登录日志领域模型
     * @return 登录日志数据对象
     */
    public static SysLogininforDO toDO(SysLogininfor source) {
        SysLogininforDO target = new SysLogininforDO();
        target.setInfoId(source.getInfoId());
        target.setLoginName(source.getLoginName());
        target.setIpaddr(source.getIpaddr());
        target.setLoginLocation(source.getLoginLocation());
        target.setBrowser(source.getBrowser());
        target.setOs(source.getOs());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setMsg(source.getMsg());
        target.setLoginTime(source.getLoginTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
