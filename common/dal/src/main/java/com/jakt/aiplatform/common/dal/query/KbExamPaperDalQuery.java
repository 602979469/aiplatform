package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * 考试试卷查询参数（common-dal 专用）：字段为数据库原始类型，仅供 Mapper/XML 使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamPaperDalQuery extends DalPageQuery {

    /** 主键。 */
    private Long id;

    /** 答题用户ID（auth_user.user_id）。 */
    private Long userId;

    /** 试卷标题。 */
    private String title;

    /** 组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）。 */
    private String mode;

    /** 状态（IN_PROGRESS进行中/GRADED已判分/ABANDONED已放弃）。 */
    private String status;

    /** 每题限时（秒）。 */
    private Integer perQuestionSeconds;

    /** 整卷限时（秒）= 题目数 × 每题限时。 */
    private Integer timeLimitSeconds;

    /** 题目数量。 */
    private Integer questionCount;

    /** 试卷总分。 */
    private Integer totalScore;

    /** 得分。 */
    private Integer score;

    /** 答对题数。 */
    private Integer correctCount;

    /** 答错题数。 */
    private Integer wrongCount;

    /** 未作答题数。 */
    private Integer unansweredCount;

    /** 开始时间。 */
    private LocalDateTime startTime;

    /** 截止时间（开始时间 + 整卷限时）。 */
    private LocalDateTime deadline;

    /** 交卷时间。 */
    private LocalDateTime submitTime;

    /** 实际用时（秒）。 */
    private Integer costSeconds;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 备注。 */
    private String remark;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
