package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.util.List;

/**
 * 家具类型大类（由后端配置提供，前端下拉框数据源）。
 */
@Data
public class FurnitureTypeGroup {

    /** 大类编码。 */
    private String code;

    /** 大类名称，如 硬装 / 门窗 / 全屋定制 / 软装 / 家电。 */
    private String name;

    /** 大类图标（简易素材标识，前端直接渲染）。 */
    private String icon;

    /** 小类列表，如 冰箱 / 空调 / 电脑。 */
    private List<FurnitureTypeItem> children;
}
