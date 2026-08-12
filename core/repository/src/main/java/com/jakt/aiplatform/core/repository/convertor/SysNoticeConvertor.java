package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeDO;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.enums.NoticeTypeEnum;
import com.jakt.aiplatform.core.model.enums.NoticeStatusEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 通知公告 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysNoticeConvertor {

    private SysNoticeConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysNoticeDO 通知公告数据对象；为空返回 null
     * @return 通知公告领域模型
     */
    public static SysNotice toModel(SysNoticeDO source) {
        if (source == null) {
            return null;
        }
        SysNotice target = new SysNotice();
        target.setNoticeId(source.getNoticeId());
        target.setNoticeTitle(source.getNoticeTitle());
        target.setNoticeType(NoticeTypeEnum.fromCode(source.getNoticeType()));
        target.setNoticeContent(source.getNoticeContent());
        target.setStatus(NoticeStatusEnum.fromCode(source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysNotice 通知公告领域模型
     * @return 通知公告数据对象
     */
    public static SysNoticeDO toDO(SysNotice source) {
        SysNoticeDO target = new SysNoticeDO();
        target.setNoticeId(source.getNoticeId());
        target.setNoticeTitle(source.getNoticeTitle());
        target.setNoticeType(ObjectUtil.isNull(source.getNoticeType()) ? null : source.getNoticeType().getCode());
        target.setNoticeContent(source.getNoticeContent());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
