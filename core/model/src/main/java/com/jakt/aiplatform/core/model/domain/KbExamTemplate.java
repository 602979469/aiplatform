package com.jakt.aiplatform.core.model.domain;


import com.jakt.aiplatform.common.framework.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷模板领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamTemplate extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 模板名称。 */
    private String name;

    /** 模板说明。 */
    private String description;

    /** 范围（GLOBAL全局/PERSONAL个人）。 */
    private String scope;

    /** 归属用户ID（scope=PERSONAL）。 */
    private Long ownerUserId;

    /** 状态（DRAFT草稿/PUBLISHED已发布/DISABLED已停用）。 */
    private String status;

    /** 组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）。 */
    private String mode;

    /** 总题量。 */
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
    private String createBy;

    /** 更新者。 */
    private String updateBy;

}
