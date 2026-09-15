package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

import com.jakt.aiplatform.web.param.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新用户题目掌握状态请求 DTO。
 *
 * <p>校验规则与 kb_user_question_stat 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbUserQuestionStatUpdateRequest extends BaseRequest {
    /** 用户ID（auth_user.user_id）。 */
    @NotNull(message = "用户ID（auth_user.user_id）不能为空")
    private Long userId;

    /** 题目ID（kb_question.id）。 */
    @NotNull(message = "题目ID（kb_question.id）不能为空")
    private Long questionId;

    /** 答对次数。 */
    private Integer rightCount;

    /** 答错次数。 */
    private Integer wrongCount;

    /** 最近一次结果（0错/1对）。 */
    private Integer lastResult;

    /** 最近作答时间。 */
    private LocalDateTime lastAnswerTime;

    /** 首次答对时间。 */
    private LocalDateTime firstRightTime;

    /** 是否已掌握（答对过=1，组卷排除）。 */
    private Integer mastered;

    /** 是否在错题集（1在/0已移出）。 */
    private Integer inWrongBook;

}
