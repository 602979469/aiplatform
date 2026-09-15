package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

/**
 * 同步映射预检项：给前端做"创建指引"的分步校验结果。
 */
@Data
public class CdcMappingCheck {

    /** 校验项名称（如 索引是否存在）。 */
    private String item;

    /** 结论：ERROR 阻断保存 / WARN 提醒 / INFO 说明。 */
    private String level;

    /** 是否通过。 */
    private Boolean passed;

    /** 结果说明（含修复建议）。 */
    private String message;
}
