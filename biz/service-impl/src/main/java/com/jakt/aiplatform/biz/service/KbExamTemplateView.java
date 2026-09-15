package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷模板视图（模板主表 + 知识点规则）。
 */
@Data
public class KbExamTemplateView {

    /** 模板ID。 */
    private Long id;

    /** 模板名称。 */
    private String name;

    /** 说明。 */
    private String description;

    /** 范围（GLOBAL全局/PERSONAL个人）。 */
    private String scope;

    /** 状态（DRAFT/PUBLISHED/DISABLED）。 */
    private String status;

    /** 归属用户ID（PERSONAL）。 */
    private Long ownerUserId;

    /** 组卷模式。 */
    private String mode;

    /** 总题量。 */
    private Integer questionCount;

    /** 每题秒数。 */
    private Integer perQuestionSeconds;

    /** 是否只出客观题。 */
    private Integer objectiveOnly;

    /** 是否排除已做对题目。 */
    private Integer excludeMastered;

    /** 使用次数。 */
    private Integer useCount;

    /** 更新时间。 */
    private LocalDateTime updateTime;

    /** 知识点规则。 */
    private List<KbExamRuleParam> rules;
}
