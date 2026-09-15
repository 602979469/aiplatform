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

    /** 题型过滤（可多选，逗号分隔）：单选/多选/判断/解答。 */
    @Size(max = 64, message = "题型参数过长")
    private String questionType;

    /** 技术方向过滤（可多选，逗号分隔）。 */
    @Size(max = 512, message = "技术方向参数过长")
    private String category;

    /** 知识点/子主题过滤（可多选，逗号分隔）。 */
    @Size(max = 512, message = "知识点参数过长")
    private String subtopic;

    /** 难度过滤（easy/medium/hard，可多选）。 */
    @Size(max = 64, message = "难度参数过长")
    private String difficulty;

    /** 页码。 */
    @Min(value = 1, message = "页码最小为 1")
    private Integer pageNum = 1;

    /** 每页条数。 */
    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 50, message = "每页条数最大为 50")
    private Integer pageSize = 10;
}
