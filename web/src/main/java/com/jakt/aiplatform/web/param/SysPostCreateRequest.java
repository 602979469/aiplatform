package com.jakt.aiplatform.web.param;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.jakt.aiplatform.core.model.enums.PostStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建岗位请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPostCreateRequest extends BaseRequest {

    /** 岗位编码。 */
    @NotBlank(message = "岗位编码不能为空")
    @Size(max = 64, message = "岗位编码长度不能超过 64")
    private String postCode;

    /** 岗位名称。 */
    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 50, message = "岗位名称长度不能超过 50")
    private String postName;

    /** 显示顺序。 */
    @NotBlank(message = "显示顺序不能为空")
    private String postSort;

    /** 状态（0正常 1停用）。 */
    @NotNull(message = "状态（0正常 1停用）不能为空")
    private PostStatusEnum status;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
