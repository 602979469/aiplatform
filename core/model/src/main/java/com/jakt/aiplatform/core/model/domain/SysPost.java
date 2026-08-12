package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.PostStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPost extends BaseModel {
    /** 主键。 */
    private Long postId;

    /** 岗位编码。 */
    private String postCode;

    /** 岗位名称。 */
    private String postName;

    /** 显示顺序。 */
    private String postSort;

    /** 状态（0正常 1停用）。 */
    private PostStatusEnum status;

    /** 备注。 */
    private String remark;

}
