package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import com.jakt.aiplatform.web.param.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建考试试卷请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamPaperCreateRequest extends BaseRequest {

    /** 答题用户ID（auth_user.user_id）。 */
    @NotNull(message = "答题用户ID（auth_user.user_id）不能为空")
    private Long userId;

    /** 试卷标题。 */
    @Size(max = 128, message = "试卷标题长度不能超过 128")
    private String title;

    /** 组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）。 */
    @Size(max = 16, message = "组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）长度不能超过 16")
    private String mode;

    /** 状态（IN_PROGRESS进行中/GRADED已判分/ABANDONED已放弃）。 */
    @Size(max = 16, message = "状态（IN_PROGRESS进行中/GRADED已判分/ABANDONED已放弃）长度不能超过 16")
    private String status;

    /** 每题限时（秒）。 */
    private Integer perQuestionSeconds;

    /** 整卷限时（秒）= 题目数 × 每题限时。 */
    @NotNull(message = "整卷限时（秒）= 题目数 × 每题限时不能为空")
    private Integer timeLimitSeconds;

    /** 题目数量。 */
    @NotNull(message = "题目数量不能为空")
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
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /** 截止时间（开始时间 + 整卷限时）。 */
    @NotNull(message = "截止时间（开始时间 + 整卷限时）不能为空")
    private LocalDateTime deadline;

    /** 交卷时间。 */
    private LocalDateTime submitTime;

    /** 实际用时（秒）。 */
    private Integer costSeconds;

    /** 创建者。 */
    @Size(max = 64, message = "创建者长度不能超过 64")
    private String createBy;

    /** 更新者。 */
    @Size(max = 64, message = "更新者长度不能超过 64")
    private String updateBy;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
