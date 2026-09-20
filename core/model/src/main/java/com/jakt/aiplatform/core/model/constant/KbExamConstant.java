package com.jakt.aiplatform.core.model.constant;

/**
 * 考题领域常量：模板范围/状态/模式、试卷状态、题型与分值配比。
 */
public final class KbExamConstant {

    /** 模板范围：全局。 */
    public static final String SCOPE_GLOBAL = "GLOBAL";

    /** 模板范围：个人。 */
    public static final String SCOPE_PERSONAL = "PERSONAL";

    /** 模板状态：已发布。 */
    public static final String TEMPLATE_STATUS_PUBLISHED = "PUBLISHED";

    /** 试卷状态：进行中。 */
    public static final String PAPER_STATUS_IN_PROGRESS = "IN_PROGRESS";

    /** 试卷状态：已判分。 */
    public static final String PAPER_STATUS_GRADED = "GRADED";

    /** 组卷模式：新题。 */
    public static final String MODE_NORMAL = "NORMAL";

    /** 题型：单选。 */
    public static final String QUESTION_TYPE_SINGLE = "单选";

    /** 题型：多选。 */
    public static final String QUESTION_TYPE_MULTI = "多选";

    /** 题型：判断。 */
    public static final String QUESTION_TYPE_JUDGE = "判断";

    /** 题型：解答。 */
    public static final String QUESTION_TYPE_ESSAY = "解答";

    /** 默认每题秒数。 */
    public static final int DEFAULT_PER_QUESTION_SECONDS = 60;

    /** 默认题量。 */
    public static final int DEFAULT_QUESTION_COUNT = 10;

    /** 客观题每题分值。 */
    public static final int SCORE_OBJECTIVE = 1;

    /** 解答题每题分值（满分，最终得分由 AI 判分决定）。 */
    public static final int SCORE_ESSAY = 5;

    /** 题型配比：选择题份数。 */
    public static final int RATIO_SELECT = 5;

    /** 题型配比：问答题份数。 */
    public static final int RATIO_QA = 2;

    /** 题型配比：解答题份数。 */
    public static final int RATIO_ESSAY = 1;

    /** 配比总份数。 */
    public static final int RATIO_TOTAL = RATIO_SELECT + RATIO_QA + RATIO_ESSAY;

    /** 列表查询条数上限（模板列表等）。 */
    public static final int LIST_LIMIT = 200;

    /** AI 判分场景码。 */
    public static final String AI_SCENE_EXAM = "EXAM";

    /** AI 判分能力码。 */
    public static final String AI_CAPABILITY_GRADING = "ANSWER_GRADING";

    private KbExamConstant() {
    }
}
