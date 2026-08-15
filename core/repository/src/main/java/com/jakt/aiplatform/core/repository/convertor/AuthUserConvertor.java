package com.jakt.aiplatform.core.repository.convertor;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthUserDO;
import com.jakt.aiplatform.common.dal.query.AuthUserDalQuery;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;


/**
 * 用户表 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AuthUserConvertor {

    private AuthUserConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param authUserDO 用户表数据对象；为空返回 null
     * @return 用户表领域模型
     */
    public static AuthUser toModel(AuthUserDO source) {
        if (source == null) {
            return null;
        }
        AuthUser target = new AuthUser();
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setNickname(source.getNickname());
        target.setPassword(source.getPassword());
        target.setEmail(source.getEmail());
        target.setAvatar(source.getAvatar());
        target.setStatus(BaseEnum.fromCode(EnableStatusEnum.class, source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param authUser 用户表领域模型
     * @return 用户表数据对象
     */
    public static AuthUserDO toDO(AuthUser source) {
        if (source == null) {
            return null;
        }
        AuthUserDO target = new AuthUserDO();
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setNickname(source.getNickname());
        target.setPassword(source.getPassword());
        target.setEmail(source.getEmail());
        target.setAvatar(source.getAvatar());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    public static AuthUserDalQuery toDalQuery(AuthUserQueryParam source) {
        AuthUserDalQuery target = new AuthUserDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setNickname(source.getNickname());
        target.setEmail(source.getEmail());
        target.setAvatar(source.getAvatar());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
