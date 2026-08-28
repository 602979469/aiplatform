package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogQueryRequest extends PageQueryRequest{

    /**
     * 文件名
     */
    private String fileName;
}
