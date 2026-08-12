package com.jakt.aiplatform.core.model.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户岗位关联领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserPost extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 用户ID。 */
    private Long userId;

    /** 岗位ID。 */
    private Long postId;

}
