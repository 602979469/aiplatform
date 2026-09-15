package com.jakt.aiplatform.web.result;

import java.time.LocalDateTime;

import com.jakt.aiplatform.web.result.BaseResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户题目掌握状态响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KbUserQuestionStatResponse extends BaseResult {
    /** 主键。 */
    private Long id;

    /** 用户ID（auth_user.user_id）。 */
    private Long userId;

    /** 题目ID（kb_question.id）。 */
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
