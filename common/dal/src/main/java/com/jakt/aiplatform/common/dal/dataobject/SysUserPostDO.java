package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户岗位关联 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserPostDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 用户ID。 */
    private Long userId;

    /** 岗位ID。 */
    private Long postId;

}
