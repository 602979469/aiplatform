package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单题作答请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamAnswerRequest extends BaseRequest {

    /** 题号（从 1 开始）。 */
    @NotNull(message = "题号不能为空")
    private Integer seq;

    /** 作答内容（单选/判断 A；多选 A,B；解答题为文本）。 */
    private String userAnswer;

    /** 本题耗时（秒，可空）。 */
    private Integer costSeconds;
}
