package com.jakt.aiplatform.core.model.param;

import lombok.Data;

/**
 * 组卷抽题参数：按知识点/题型/难度筛选，并可排除该用户已做对的题目。
 */
@Data
public class KbQuestionPickParam {

    /** 答题用户ID（excludeMastered=true 时用于排除已掌握题目）。 */
    private Long userId;

    /** 分类（知识点一级）。 */
    private String category;

    /** 子主题（知识点二级，空=该分类全部）。 */
    private String subtopic;

    /** 题型（空=不限）。 */
    private String questionType;

    /** 难度（空=不限）。 */
    private String difficulty;

    /** 是否排除已做对的题目。 */
    private boolean excludeMastered = true;

    /** 抽题数量。 */
    private int limit = 10;
}
