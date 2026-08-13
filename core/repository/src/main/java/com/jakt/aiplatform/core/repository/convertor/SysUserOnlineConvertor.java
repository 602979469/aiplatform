package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserOnlineDO;
import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;
import cn.hutool.core.convert.Convert;

/**
 * 在线用户 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserOnlineConvertor {

    private SysUserOnlineConvertor() {
    }

    /**
     * 领域模型 → 查询参数（expireTime Long 转 Integer，显式赋值）。
     *
     * @param userOnline 在线用户领域模型
     * @return 在线用户查询参数
     */
    public static SysUserOnlineQueryParam toQueryParam(SysUserOnline userOnline) {
        SysUserOnlineQueryParam query = new SysUserOnlineQueryParam();
        query.setSessionId(userOnline.getSessionId());
        query.setLoginName(userOnline.getLoginName());
        query.setDeptName(userOnline.getDeptName());
        query.setIpaddr(userOnline.getIpaddr());
        query.setLoginLocation(userOnline.getLoginLocation());
        query.setBrowser(userOnline.getBrowser());
        query.setOs(userOnline.getOs());
        query.setStatus(userOnline.getStatus());
        query.setStartTimestamp(userOnline.getStartTimestamp());
        query.setLastAccessTime(userOnline.getLastAccessTime());
        query.setExpireTime(userOnline.getExpireTime() == null ? null : userOnline.getExpireTime().intValue());
        query.setSessionData(userOnline.getSessionData());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 在线用户数据对象（条件载体）
     * @return 在线用户查询参数
     */
    public static SysUserOnlineQueryParam toQueryParam(SysUserOnlineDO condition) {
        SysUserOnlineQueryParam query = new SysUserOnlineQueryParam();
        query.setSessionId(condition.getSessionId());
        query.setLoginName(condition.getLoginName());
        query.setDeptName(condition.getDeptName());
        query.setIpaddr(condition.getIpaddr());
        query.setLoginLocation(condition.getLoginLocation());
        query.setBrowser(condition.getBrowser());
        query.setOs(condition.getOs());
        query.setStatus(condition.getStatus());
        query.setStartTimestamp(condition.getStartTimestamp());
        query.setLastAccessTime(condition.getLastAccessTime());
        query.setExpireTime(condition.getExpireTime());
        query.setSessionData(condition.getSessionData());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysUserOnlineDO 在线用户数据对象；为空返回 null
     * @return 在线用户领域模型
     */
    public static SysUserOnline toModel(SysUserOnlineDO source) {
        if (source == null) {
            return null;
        }
        SysUserOnline target = new SysUserOnline();
        target.setSessionId(source.getSessionId());
        target.setLoginName(source.getLoginName());
        target.setDeptName(source.getDeptName());
        target.setIpaddr(source.getIpaddr());
        target.setLoginLocation(source.getLoginLocation());
        target.setBrowser(source.getBrowser());
        target.setOs(source.getOs());
        target.setStatus(source.getStatus());
        target.setStartTimestamp(source.getStartTimestamp());
        target.setLastAccessTime(source.getLastAccessTime());
        target.setExpireTime(Convert.toLong(source.getExpireTime()));
        target.setSessionData(source.getSessionData());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysUserOnline 在线用户领域模型
     * @return 在线用户数据对象
     */
    public static SysUserOnlineDO toDO(SysUserOnline source) {
        SysUserOnlineDO target = new SysUserOnlineDO();
        target.setSessionId(source.getSessionId());
        target.setLoginName(source.getLoginName());
        target.setDeptName(source.getDeptName());
        target.setIpaddr(source.getIpaddr());
        target.setLoginLocation(source.getLoginLocation());
        target.setBrowser(source.getBrowser());
        target.setOs(source.getOs());
        target.setStatus(source.getStatus());
        target.setStartTimestamp(source.getStartTimestamp());
        target.setLastAccessTime(source.getLastAccessTime());
        target.setExpireTime(Convert.toInt(source.getExpireTime()));
        target.setSessionData(source.getSessionData());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
