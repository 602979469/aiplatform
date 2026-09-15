package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.util.List;

/**
 * 同步映射预检结果：校验项列表 + 将要写入 ConfigMap 的 yml 预览。
 */
@Data
public class CdcMappingPreview {

    /** 是否存在阻断项（ERROR）。 */
    private Boolean pass;

    /** 校验项列表。 */
    private List<CdcMappingCheck> checks;

    /** 生成的 yml 文本（保存后写入 ConfigMap 的内容）。 */
    private String yml;
}
