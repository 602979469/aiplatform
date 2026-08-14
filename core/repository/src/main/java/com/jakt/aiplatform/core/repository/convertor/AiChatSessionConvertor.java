package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.AiChatSessionDO;
import com.jakt.aiplatform.common.dal.query.AiChatSessionDalQuery;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.AiChatSessionStatusEnum;
import com.jakt.aiplatform.core.model.enums.BaseEnum;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;


/**
 * 用户AI会话 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AiChatSessionConvertor {

    private AiChatSessionConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param aiChatSessionDO 用户AI会话数据对象；为空返回 null
     * @return 用户AI会话领域模型
     */
    public static AiChatSession toModel(AiChatSessionDO source) {
        if (source == null) {
            return null;
        }
        AiChatSession target = new AiChatSession();
        target.setSessionId(source.getSessionId());
        target.setSessionName(source.getSessionName());
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setStatus(BaseEnum.fromCode(AiChatSessionStatusEnum.class, source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param aiChatSession 用户AI会话领域模型
     * @return 用户AI会话数据对象
     */
    public static AiChatSessionDO toDO(AiChatSession source) {
        if (source == null) {
            return null;
        }
        AiChatSessionDO target = new AiChatSessionDO();
        target.setSessionId(source.getSessionId());
        target.setSessionName(source.getSessionName());
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setStatus(source.getStatus() == null ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    public static AiChatSessionDalQuery toDalQuery(AiChatSessionQueryParam source) {
        AiChatSessionDalQuery target = new AiChatSessionDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setSessionId(source.getSessionId());
        target.setSessionName(source.getSessionName());
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setStatus(source.getStatus());
        target.setRemark(source.getRemark());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
