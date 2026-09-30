package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.util.tools.JsonUtil;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeGroup;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeItem;
import com.jakt.aiplatform.core.service.FurnitureTypeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 家具类型领域服务实现：类型树配置在 application.yml（aiplatform.home.furniture-types），
 * 环境变量 {@code AIPLATFORM_HOME_FURNITURE_TYPES} 可整体覆盖；首次访问解析后缓存，改配置需重启。
 */
@Service
public class FurnitureTypeServiceImpl implements FurnitureTypeService {

    /** 家具类型配置（JSON 数组，两级：大类 → 小类）。 */
    @Value("${aiplatform.home.furniture-types:[]}")
    private String furnitureTypesJson;

    /** 解析缓存。 */
    private volatile List<FurnitureTypeGroup> groups;

    @Override
    public List<FurnitureTypeGroup> listGroups() {
        return loadGroups();
    }

    @Override
    public FurnitureTypeGroup findGroup(String bigTypeCode) {
        if (StrUtil.isBlank(bigTypeCode)) {
            return null;
        }
        for (FurnitureTypeGroup group : loadGroups()) {
            if (StrUtil.equals(group.getCode(), bigTypeCode)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public FurnitureTypeItem findType(String bigTypeCode, String typeCode) {
        if (StrUtil.isBlank(typeCode)) {
            return null;
        }
        FurnitureTypeGroup group = findGroup(bigTypeCode);
        if (ObjectUtil.isNull(group) || CollUtil.isEmpty(group.getChildren())) {
            return null;
        }
        for (FurnitureTypeItem item : group.getChildren()) {
            if (StrUtil.equals(item.getCode(), typeCode)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 懒加载解析配置（配置为空视为未配置类型）。
     */
    private List<FurnitureTypeGroup> loadGroups() {
        List<FurnitureTypeGroup> current = groups;
        if (ObjectUtil.isNull(current)) {
            current = StrUtil.isBlank(furnitureTypesJson)
                    ? List.of()
                    : JsonUtil.parseArray(furnitureTypesJson, FurnitureTypeGroup.class);
            groups = current;
        }
        return current;
    }
}
