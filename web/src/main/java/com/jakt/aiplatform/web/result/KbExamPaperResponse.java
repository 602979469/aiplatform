package com.jakt.aiplatform.web.result;

import java.time.LocalDateTime;

import com.jakt.aiplatform.web.result.BaseResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 考试试卷响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KbExamPaperResponse extends BaseResult {
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

    /** 组卷分类与题量快照（[{category,count}]）。 */
    private String categories;

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

}
