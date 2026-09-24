package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

/**
 * 题库题型题量统计（组卷容量数据源）。
 *
 * <p>表示"某个知识点范围内、某个题型还能抽多少道题"，用于组卷配额分配，
 * 以及题量不足时给出明确的失败原因。</p>
 */
@Data
public class KbQuestionTypeStat {

    /** 题型（单选 / 多选 / 判断 / 解答）。 */
    private String questionType;

    /** 该题型在当前知识点范围内的可用题量。 */
    private long total;
}
