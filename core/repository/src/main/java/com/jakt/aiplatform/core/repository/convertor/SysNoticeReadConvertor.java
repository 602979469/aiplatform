package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeReadDO;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import cn.hutool.core.convert.Convert;

/**
 * 公告已读记录 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysNoticeReadConvertor {

    private SysNoticeReadConvertor() {
    }

    /**
     * 领域模型 → 查询参数（noticeId Long 转 Integer，显式赋值）。
     *
     * @param noticeRead 公告已读记录领域模型
     * @return 公告已读记录查询参数
     */
    public static SysNoticeReadQueryParam toQueryParam(SysNoticeRead noticeRead) {
        SysNoticeReadQueryParam query = new SysNoticeReadQueryParam();
        query.setReadId(noticeRead.getReadId());
        query.setNoticeId(noticeRead.getNoticeId() == null ? null : noticeRead.getNoticeId().intValue());
        query.setUserId(noticeRead.getUserId());
        query.setReadTime(noticeRead.getReadTime());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 公告已读记录数据对象（条件载体）
     * @return 公告已读记录查询参数
     */
    public static SysNoticeReadQueryParam toQueryParam(SysNoticeReadDO condition) {
        SysNoticeReadQueryParam query = new SysNoticeReadQueryParam();
        query.setReadId(condition.getReadId());
        query.setNoticeId(condition.getNoticeId());
        query.setUserId(condition.getUserId());
        query.setReadTime(condition.getReadTime());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysNoticeReadDO 公告已读记录数据对象；为空返回 null
     * @return 公告已读记录领域模型
     */
    public static SysNoticeRead toModel(SysNoticeReadDO source) {
        if (source == null) {
            return null;
        }
        SysNoticeRead target = new SysNoticeRead();
        target.setReadId(source.getReadId());
        target.setNoticeId(Convert.toLong(source.getNoticeId()));
        target.setUserId(source.getUserId());
        target.setReadTime(source.getReadTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysNoticeRead 公告已读记录领域模型
     * @return 公告已读记录数据对象
     */
    public static SysNoticeReadDO toDO(SysNoticeRead source) {
        SysNoticeReadDO target = new SysNoticeReadDO();
        target.setReadId(source.getReadId());
        target.setNoticeId(Convert.toInt(source.getNoticeId()));
        target.setUserId(source.getUserId());
        target.setReadTime(source.getReadTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
