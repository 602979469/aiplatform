package com.jakt.aiplatform.common.dal.query;

import lombok.Data;

/**
 * 用户题目掌握度增量更新参数（按 user_id + question_id 幂等 upsert）。
 */
@Data
public class KbQuestionStatDelta {

    /** 用户ID。 */
    private Long userId;

    /** 题目ID。 */
    private Long questionId;

    /** 本次答对增量。 */
    private int rightDelta;

    /** 本次答错增量。 */
    private int wrongDelta;

    /** 最近一次结果（1对 / 0错）。 */
    private Integer lastResult;

    /** 是否已掌握（1是）。 */
    private Integer mastered;

    /** 是否在错题集（1在 / 0移出）。 */
    private Integer inWrongBook;
}
