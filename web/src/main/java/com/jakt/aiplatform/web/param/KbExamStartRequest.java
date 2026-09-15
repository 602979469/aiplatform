package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 开始考试请求：选模板（templateId）或快速创建（rules + 题量/限时）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamStartRequest extends BaseRequest {

    /** 试卷模板ID（可空；填了则以模板为准）。 */
    private Long templateId;

    /** 试卷标题（可空）。 */
    private String title;

    /** 组卷模式（NORMAL新题/WRONG_BOOK错题重练/REVIEW复习）。 */
    private String mode;

    /** 总题量（快速创建时使用）。 */
    private Integer questionCount;

    /** 每题秒数（默认 60）。 */
    private Integer perQuestionSeconds;

    /** 是否排除已做对的题目（1是/0否，默认 1）。 */
    private Integer excludeMastered;

    /** 是否只出客观题（1是/0否，默认 1）。 */
    private Integer objectiveOnly;

    /** 知识点规则（快速创建时使用）。 */
    private List<Rule> rules;

    /**
     * 知识点规则。
     */
    @Data
    public static class Rule {

        /** 分类。 */
        private String category;

        /** 子主题（可空=该分类全部）。 */
        private String subtopic;

        /** 限定题型（可空）。 */
        private String questionType;

        /** 限定难度（可空）。 */
        private String difficulty;

        /** 抽题数量。 */
        private Integer count;
    }
}
