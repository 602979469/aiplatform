package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * 试卷题目快照与作答查询参数（common-dal 专用）：字段为数据库原始类型，仅供 Mapper/XML 使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamPaperQuestionDalQuery extends DalPageQuery {

    /** 主键。 */
    private Long id;

    /** 试卷ID（kb_exam_paper.id）。 */
    private Long paperId;

    /** 来源题目ID（kb_question.id，用于溯源与掌握度统计）。 */
    private Long questionId;

    /** 题号（从 1 开始）。 */
    private Integer seq;

    /** 题型（单选/多选/判断）。 */
    private String questionType;

    /** 分类。 */
    private String category;

    /** 子主题。 */
    private String subtopic;

    /** 题干。 */
    private String title;

    /** 题目内容（Markdown/HTML）。 */
    private String content;

    /** 正确答案快照（如 A,B,C）。 */
    private String answer;

    /** 解析快照。 */
    private String explanation;

    /** 难度。 */
    private String difficulty;

    /** 本题分值。 */
    private Integer score;

    /** 用户作答（如 A,B）。 */
    private String userAnswer;

    /** 是否正确（NULL未判/0错/1对）。 */
    private Integer isCorrect;

    /** 本题作答耗时（秒，前端上报）。 */
    private Integer answerCostSeconds;

    /** 作答时间。 */
    private LocalDateTime answerTime;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
