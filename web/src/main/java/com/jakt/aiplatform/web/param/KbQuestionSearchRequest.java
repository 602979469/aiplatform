package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题库搜索请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestionSearchRequest extends BaseRequest {

    /** 关键词。 */
    @Size(max = 100, message = "关键词长度不能超过 100")
    private String keyword;

    /** 页码。 */
    @Min(value = 1, message = "页码最小为 1")
    private Integer pageNum = 1;

    /** 每页条数。 */
    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 50, message = "每页条数最大为 50")
    private Integer pageSize = 10;
}
