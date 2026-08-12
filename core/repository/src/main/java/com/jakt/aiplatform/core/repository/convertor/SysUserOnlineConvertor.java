package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserOnlineDO;
import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import cn.hutool.core.convert.Convert;


/**
 * 在线用户 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserOnlineConvertor {

    private SysUserOnlineConvertor() {
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
