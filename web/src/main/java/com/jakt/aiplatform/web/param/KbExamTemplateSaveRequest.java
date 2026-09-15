package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 试卷模板保存请求（模板主表 + 知识点规则）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamTemplateSaveRequest extends BaseRequest {

    /** 模板ID（修改时必填）。 */
    private Long id;

    /** 模板名称。 */
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 128, message = "模板名称长度不能超过 128")
    private String name;

    /** 说明。 */
    @Size(max = 500, message = "说明长度不能超过 500")
    private String description;

    /** 范围（GLOBAL全局/PERSONAL个人）。 */
    private String scope;

    /** 状态（DRAFT/PUBLISHED/DISABLED）。 */
    private String status;

    /** 组卷模式（NORMAL/REVIEW）。 */
    private String mode;

    /** 总题量（不填则取规则题量之和）。 */
    private Integer questionCount;

    /** 每题秒数。 */
    private Integer perQuestionSeconds;

    /** 是否只出客观题。 */
    private Integer objectiveOnly;

    /** 是否排除已做对题目。 */
    private Integer excludeMastered;

    /** 知识点规则。 */
    private List<KbExamStartRequest.Rule> rules;
}
