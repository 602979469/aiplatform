package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.jakt.aiplatform.web.param.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新试卷题目快照与作答请求 DTO。
 *
 * <p>校验规则与 kb_exam_paper_question 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamPaperQuestionUpdateRequest extends BaseRequest {
    /** 试卷ID（kb_exam_paper.id）。 */
    @NotNull(message = "试卷ID（kb_exam_paper.id）不能为空")
    private Long paperId;

    /** 来源题目ID（kb_question.id，用于溯源与掌握度统计）。 */
    @NotNull(message = "来源题目ID（kb_question.id，用于溯源与掌握度统计）不能为空")
    private Long questionId;

    /** 题号（从 1 开始）。 */
    @NotNull(message = "题号（从 1 开始）不能为空")
    private Integer seq;

    /** 题型（单选/多选/判断）。 */
    @NotBlank(message = "题型（单选/多选/判断）不能为空")
    @Size(max = 16, message = "题型（单选/多选/判断）长度不能超过 16")
    private String questionType;

    /** 分类。 */
    @NotBlank(message = "分类不能为空")
    @Size(max = 64, message = "分类长度不能超过 64")
    private String category;

    /** 子主题。 */
    @Size(max = 64, message = "子主题长度不能超过 64")
    private String subtopic;

    /** 题干。 */
    @NotBlank(message = "题干不能为空")
    @Size(max = 255, message = "题干长度不能超过 255")
    private String title;

    /** 题目内容（Markdown/HTML）。 */
    @Size(max = 16777215, message = "题目内容（Markdown/HTML）长度不能超过 16777215")
    private String content;

    /** 选项快照（[{key,text}]）。 */
    private String options;

    /** 正确答案快照（如 A,B,C）。 */
    @Size(max = 512, message = "正确答案快照（如 A,B,C）长度不能超过 512")
    private String answer;

    /** 解析快照。 */
    @Size(max = 65535, message = "解析快照长度不能超过 65535")
    private String explanation;

    /** 难度。 */
    @Size(max = 16, message = "难度长度不能超过 16")
    private String difficulty;

    /** 本题分值。 */
    private Integer score;

    /** 用户作答（如 A,B）。 */
    @Size(max = 512, message = "用户作答（如 A,B）长度不能超过 512")
    private String userAnswer;

    /** 是否正确（NULL未判/0错/1对）。 */
    private Integer isCorrect;

    /** 本题作答耗时（秒，前端上报）。 */
    private Integer answerCostSeconds;

    /** 作答时间。 */
    private LocalDateTime answerTime;

}
