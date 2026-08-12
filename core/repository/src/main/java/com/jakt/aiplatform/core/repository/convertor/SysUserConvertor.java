package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserDO;
import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.enums.UserTypeEnum;
import com.jakt.aiplatform.core.model.enums.SexEnum;
import com.jakt.aiplatform.core.model.enums.UserStatusEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 用户 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserConvertor {

    private SysUserConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysUserDO 用户数据对象；为空返回 null
     * @return 用户领域模型
     */
    public static SysUser toModel(SysUserDO source) {
        if (source == null) {
            return null;
        }
        SysUser target = new SysUser();
        target.setUserId(source.getUserId());
        target.setDeptId(source.getDeptId());
        target.setLoginName(source.getLoginName());
        target.setUserName(source.getUserName());
        target.setUserType(UserTypeEnum.fromCode(source.getUserType()));
        target.setEmail(source.getEmail());
        target.setPhonenumber(source.getPhonenumber());
        target.setSex(SexEnum.fromCode(source.getSex()));
        target.setAvatar(source.getAvatar());
        target.setPassword(source.getPassword());
        target.setSalt(source.getSalt());
        target.setStatus(UserStatusEnum.fromCode(source.getStatus()));
        target.setLoginIp(source.getLoginIp());
        target.setLoginDate(source.getLoginDate());
        target.setPwdUpdateDate(source.getPwdUpdateDate());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysUser 用户领域模型
     * @return 用户数据对象
     */
    public static SysUserDO toDO(SysUser source) {
        SysUserDO target = new SysUserDO();
        target.setUserId(source.getUserId());
        target.setDeptId(source.getDeptId());
        target.setLoginName(source.getLoginName());
        target.setUserName(source.getUserName());
        target.setUserType(ObjectUtil.isNull(source.getUserType()) ? null : source.getUserType().getCode());
        target.setEmail(source.getEmail());
        target.setPhonenumber(source.getPhonenumber());
        target.setSex(ObjectUtil.isNull(source.getSex()) ? null : source.getSex().getCode());
        target.setAvatar(source.getAvatar());
        target.setPassword(source.getPassword());
        target.setSalt(source.getSalt());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setLoginIp(source.getLoginIp());
        target.setLoginDate(source.getLoginDate());
        target.setPwdUpdateDate(source.getPwdUpdateDate());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
