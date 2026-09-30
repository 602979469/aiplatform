package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

/**
 * 家具类型小类（由后端配置提供）。
 */
@Data
public class FurnitureTypeItem {

    /** 小类编码，如 air_conditioner。 */
    private String code;

    /** 小类名称，如 空调。 */
    private String name;

    /** 小类图标（简易素材标识，前端直接渲染）。 */
    private String icon;
}
