package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPostDO extends BaseDO {
    /** 主键。 */
    private Long postId;

    /** 岗位编码。 */
    private String postCode;

    /** 岗位名称。 */
    private String postName;

    /** 显示顺序。 */
    private Integer postSort;

    /** 状态（0正常 1停用）。 */
    private String status;

    /** 备注。 */
    private String remark;

}
