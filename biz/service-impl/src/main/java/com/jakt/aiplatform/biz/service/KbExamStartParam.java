package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.util.List;

/**
 * 开始考试入参：可指定模板，也可直接给组卷规则（快速创建）。
 */
@Data
public class KbExamStartParam {

    /** 答题用户ID。 */
    private Long userId;

    /** 试卷模板ID（与 rules 二选一，模板优先）。 */
    private Long templateId;

    /** 试卷标题。 */
    private String title;

    /** 组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）。 */
    private String mode;

    /** 总题量（快速创建时使用；模板时以模板为准）。 */
    private Integer questionCount;

    /** 每题秒数（默认 60）。 */
    private Integer perQuestionSeconds;

    /** 是否排除已做对的题目（1是/0否，默认 1）。 */
    private Integer excludeMastered;

    /** 是否只出客观题（1是/0否，默认 1；解答题不参与自动判分）。 */
    private Integer objectiveOnly;

    /** 知识点规则（快速创建时使用）。 */
    private List<KbExamRuleParam> rules;
}
