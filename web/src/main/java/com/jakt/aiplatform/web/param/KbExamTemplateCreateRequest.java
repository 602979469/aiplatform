package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.jakt.aiplatform.web.param.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建试卷模板请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamTemplateCreateRequest extends BaseRequest {

    /** 模板名称。 */
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 128, message = "模板名称长度不能超过 128")
    private String name;

    /** 模板说明。 */
    @Size(max = 500, message = "模板说明长度不能超过 500")
    private String description;

    /** 范围（GLOBAL全局/PERSONAL个人）。 */
    @Size(max = 16, message = "范围（GLOBAL全局/PERSONAL个人）长度不能超过 16")
    private String scope;

    /** 归属用户ID（scope=PERSONAL）。 */
    private Long ownerUserId;

    /** 状态（DRAFT草稿/PUBLISHED已发布/DISABLED已停用）。 */
    @Size(max = 16, message = "状态（DRAFT草稿/PUBLISHED已发布/DISABLED已停用）长度不能超过 16")
    private String status;

    /** 组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）。 */
    @Size(max = 16, message = "组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）长度不能超过 16")
    private String mode;

    /** 总题量。 */
    @NotNull(message = "总题量不能为空")
    private Integer questionCount;

    /** 每题限时（秒）。 */
    private Integer perQuestionSeconds;

    /** 是否只出客观题（1是/0否，0含解答题走练习模式）。 */
    private Integer objectiveOnly;

    /** 是否排除已做对题目（1是/0否）。 */
    private Integer excludeMastered;

    /** 题型配比，如 {"单选":12,"多选":4,"判断":4}。 */
    private String typeMix;

    /** 难度配比，如 {"easy":30,"medium":50,"hard":20}。 */
    private String difficultyMix;

    /** 被使用次数。 */
    private Integer useCount;

    /** 创建者。 */
    @Size(max = 64, message = "创建者长度不能超过 64")
    private String createBy;

    /** 更新者。 */
    @Size(max = 64, message = "更新者长度不能超过 64")
    private String updateBy;

}
