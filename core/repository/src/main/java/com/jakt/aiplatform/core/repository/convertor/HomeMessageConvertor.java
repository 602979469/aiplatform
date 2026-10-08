package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.HomeMessageDO;
import com.jakt.aiplatform.core.model.domain.HomeMessage;

/**
 * 首页留言板留言 DO 与领域模型转换器。
 */
public final class HomeMessageConvertor {

    private HomeMessageConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param source 留言数据对象；为空返回 null
     * @return 留言领域模型
     */
    public static HomeMessage toModel(HomeMessageDO source) {
        if (source == null) {
            return null;
        }
        HomeMessage target = new HomeMessage();
        target.setId(source.getId());
        target.setContent(source.getContent());
        target.setClientIp(source.getClientIp());
        target.setAvatarFileId(source.getAvatarFileId());
        target.setAvatarName(source.getAvatarName());
        target.setColorIndex(source.getColorIndex());
        target.setCreateBy(source.getCreateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param source 留言领域模型；为空返回 null
     * @return 留言数据对象
     */
    public static HomeMessageDO toDO(HomeMessage source) {
        if (source == null) {
            return null;
        }
        HomeMessageDO target = new HomeMessageDO();
        target.setId(source.getId());
        target.setContent(source.getContent());
        target.setClientIp(source.getClientIp());
        target.setAvatarFileId(source.getAvatarFileId());
        target.setAvatarName(source.getAvatarName());
        target.setColorIndex(source.getColorIndex());
        target.setCreateBy(source.getCreateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
