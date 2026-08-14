package com.jakt.aiplatform.core.repository.convertor;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthLoginLogDO;
import com.jakt.aiplatform.common.dal.query.AuthLoginLogDalQuery;
import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.LoginLogStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;


/**
 * 登录记录表 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AuthLoginLogConvertor {

    private AuthLoginLogConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param authLoginLogDO 登录记录表数据对象；为空返回 null
     * @return 登录记录表领域模型
     */
    public static AuthLoginLog toModel(AuthLoginLogDO source) {
        if (source == null) {
            return null;
        }
        AuthLoginLog target = new AuthLoginLog();
        target.setLogId(source.getLogId());
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setLoginIp(source.getLoginIp());
        target.setUserAgent(source.getUserAgent());
        target.setStatus(BaseEnum.fromCode(LoginLogStatusEnum.class, source.getStatus()));
        target.setMessage(source.getMessage());
        target.setLoginTime(source.getLoginTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param authLoginLog 登录记录表领域模型
     * @return 登录记录表数据对象
     */
    public static AuthLoginLogDO toDO(AuthLoginLog source) {
        if (source == null) {
            return null;
        }
        AuthLoginLogDO target = new AuthLoginLogDO();
        target.setLogId(source.getLogId());
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setLoginIp(source.getLoginIp());
        target.setUserAgent(source.getUserAgent());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setMessage(source.getMessage());
        target.setLoginTime(source.getLoginTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    public static AuthLoginLogDalQuery toDalQuery(AuthLoginLogQueryParam source) {
        AuthLoginLogDalQuery target = new AuthLoginLogDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setLogId(source.getLogId());
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setLoginIp(source.getLoginIp());
        target.setUserAgent(source.getUserAgent());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setMessage(source.getMessage());
        target.setLoginTime(source.getLoginTime());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
