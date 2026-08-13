package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeDO;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.enums.NoticeTypeEnum;
import com.jakt.aiplatform.core.model.enums.NoticeStatusEnum;
import com.jakt.aiplatform.core.model.param.SysNoticeQueryParam;
import com.jakt.aiplatform.core.model.result.SysNoticeListResult;
import cn.hutool.core.util.ObjectUtil;


/**
 * 通知公告 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysNoticeConvertor {

    private SysNoticeConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param notice 通知公告领域模型
     * @return 通知公告查询参数
     */
    public static SysNoticeQueryParam toQueryParam(SysNotice notice) {
        SysNoticeQueryParam query = new SysNoticeQueryParam();
        query.setNoticeId(notice.getNoticeId());
        query.setNoticeTitle(notice.getNoticeTitle());
        query.setNoticeType(notice.getNoticeType() == null ? null : notice.getNoticeType().getCode());
        query.setNoticeContent(notice.getNoticeContent());
        query.setStatus(notice.getStatus() == null ? null : notice.getStatus().getCode());
        query.setRemark(notice.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 通知公告数据对象（条件载体）
     * @return 通知公告查询参数
     */
    public static SysNoticeQueryParam toQueryParam(SysNoticeDO condition) {
        SysNoticeQueryParam query = new SysNoticeQueryParam();
        query.setNoticeId(condition.getNoticeId());
        query.setNoticeTitle(condition.getNoticeTitle());
        query.setNoticeType(condition.getNoticeType());
        query.setNoticeContent(condition.getNoticeContent());
        query.setStatus(condition.getStatus());
        query.setRemark(condition.getRemark());
        return query;
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
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 公告列表投影 → 领域模型（带已读标记）。
     *
     * @param source 公告列表投影；为空返回 null
     * @return 通知公告领域模型
     */
    public static SysNotice toModel(SysNoticeListResult source) {
        if (source == null) {
            return null;
        }
        SysNotice target = new SysNotice();
        target.setNoticeId(source.getNoticeId());
        target.setNoticeTitle(source.getNoticeTitle());
        target.setNoticeType(NoticeTypeEnum.fromCode(source.getNoticeType()));
        target.setNoticeContent(source.getNoticeContent());
        target.setStatus(NoticeStatusEnum.fromCode(source.getStatus()));
        target.setCreateBy(source.getCreateBy());
        target.setCreateTime(source.getCreateTime());
        target.setRead(source.getIsRead() == null ? false : source.getIsRead());
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
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
