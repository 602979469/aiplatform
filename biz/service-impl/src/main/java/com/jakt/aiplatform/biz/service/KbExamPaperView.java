package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷视图（答题页 / 续考）。
 */
@Data
public class KbExamPaperView {

    /** 试卷ID。 */
    private Long paperId;

    /** 标题。 */
    private String title;

    /** 模式。 */
    private String mode;

    /** 状态（IN_PROGRESS/GRADED/ABANDONED）。 */
    private String status;

    /** 题目数量。 */
    private Integer questionCount;

    /** 每题秒数。 */
    private Integer perQuestionSeconds;

    /** 整卷限时（秒）。 */
    private Integer timeLimitSeconds;

    /** 剩余秒数（已超时返回 0）。 */
    private Long remainingSeconds;

    /** 开始时间。 */
    private LocalDateTime startTime;

    /** 截止时间。 */
    private LocalDateTime deadline;

    /** 题目列表。 */
    private List<KbExamQuestionView> questions;
}
