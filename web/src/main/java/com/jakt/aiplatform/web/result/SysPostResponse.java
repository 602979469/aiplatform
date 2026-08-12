package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.PostStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 岗位响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysPostResponse extends BaseResult {
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
