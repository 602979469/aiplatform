package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.FurnitureTypeGroup;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeItem;

import java.util.List;

/**
 * 家具类型领域服务：类型树来自后端配置（大类 → 小类），供下拉框与录入校验使用。
 */
public interface FurnitureTypeService {

    /**
     * 类型树（大类含小类），供前端下拉框渲染。
     *
     * @return 类型树
     */
    List<FurnitureTypeGroup> listGroups();

    /**
     * 按编码查大类。
     *
     * @param bigTypeCode 大类编码
     * @return 大类；不在配置内返回 null
     */
    FurnitureTypeGroup findGroup(String bigTypeCode);

    /**
     * 按编码查小类。
     *
     * @param bigTypeCode 大类编码
     * @param typeCode 小类编码
     * @return 小类；不在配置内返回 null
     */
    FurnitureTypeItem findType(String bigTypeCode, String typeCode);
}
