package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogDetailRequest extends PageQueryRequest{

    /**
     * 文件名（必填）
     */
    private String fileName;

    /**
     * 关键词搜索（可选）
     */
    private String keyword;
}
