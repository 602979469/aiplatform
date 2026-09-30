package com.jakt.aiplatform.web.result;

import lombok.Data;

import java.util.List;

/**
 * 家具类型响应 DTO：大类带小类，两级结构（小类不再有 children）。
 */
@Data
public class FurnitureTypeResponse {

    /** 类型编码。 */
    private String code;

    /** 类型名称。 */
    private String name;

    /** 简易图标（后端配置的素材标识，前端直接渲染）。 */
    private String icon;

    /** 子类型；小类为空。 */
    private List<FurnitureTypeResponse> children;
}
