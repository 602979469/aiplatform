package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * 试卷模板查询参数（common-dal 专用）：字段为数据库原始类型，仅供 Mapper/XML 使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamTemplateDalQuery extends DalPageQuery {

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

    /** 被使用次数。 */
    private Integer useCount;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
