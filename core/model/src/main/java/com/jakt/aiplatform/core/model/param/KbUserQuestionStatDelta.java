package com.jakt.aiplatform.core.model.param;

import lombok.Data;

/**
 * 用户题目掌握度增量：答对/答错累加次数，并更新最近结果、掌握标记与错题集标记。
 */
@Data
public class KbUserQuestionStatDelta {

    /** 用户ID。 */
    private Long userId;

    /** 题目ID。 */
    private Long questionId;

    /** 答对次数增量。 */
    private Integer rightDelta;

    /** 答错次数增量。 */
    private Integer wrongDelta;

    /** 最近一次结果（0错/1对）。 */
    private Integer lastResult;

    /** 是否已掌握（答对过=1）。 */
    private Integer mastered;

    /** 是否在错题集（1在/0已移出）。 */
    private Integer inWrongBook;
}
