package com.jakt.aiplatform.web.result;

import lombok.Data;

import java.util.List;

/**
 * ES 同步映射预检响应（前端创建指引的数据源）。
 */
@Data
public class CdcMappingPreviewResponse {

    /** 是否可通过保存（没有 ERROR 项）。 */
    private Boolean pass;

    /** 校验项列表。 */
    private List<Check> checks;

    /** 将写入 ConfigMap 的 yml 文本。 */
    private String yml;

    /**
     * 单项校验结果。
     */
    @Data
    public static class Check {

        /** 校验项名称。 */
        private String item;

        /** 级别：ERROR/WARN/INFO。 */
        private String level;

        /** 是否通过。 */
        private Boolean pass;

        /** 说明与修复建议。 */
        private String message;
    }
}
