package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeReadDO;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import cn.hutool.core.convert.Convert;


/**
 * 公告已读记录 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysNoticeReadConvertor {

    private SysNoticeReadConvertor() {
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
