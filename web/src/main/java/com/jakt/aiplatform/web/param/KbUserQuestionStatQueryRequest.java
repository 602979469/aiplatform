package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import com.jakt.aiplatform.web.param.PageQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户题目掌握状态查询请求。分页参数由 {@link PageQueryRequest} 提供。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbUserQuestionStatQueryRequest extends PageQueryRequest {

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

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;
}
