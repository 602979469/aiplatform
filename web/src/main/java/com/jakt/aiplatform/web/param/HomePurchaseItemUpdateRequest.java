package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.constant.HomePurchaseConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新家庭装修采购项请求 DTO：全量覆盖，未传字段按空处理；fileIds 为整体替换。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemUpdateRequest extends BaseRequest {

    /** 大类编码（后端配置项，如 hardcover）。 */
    @NotBlank(message = "家具大类不能为空")
    @Size(max = 64, message = "家具大类编码长度不能超过 64")
    private String bigTypeCode;

    /** 小类编码（后端配置项，如 air_conditioner）。 */
    @NotBlank(message = "家具类型不能为空")
    @Size(max = 64, message = "家具类型编码长度不能超过 64")
    private String typeCode;

    /** 产品名称。 */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过 200")
    private String productName;

    /** 采购数量；不传按 1 处理。 */
    private Integer quantity;

    /** 预算：区间（800~1200）或精确值（999）都可。 */
    @NotBlank(message = "预算不能为空")
    @Size(max = 64, message = "预算长度不能超过 64")
    private String budgetText;

    /** 安装费（单件），不传表示无。 */
    private BigDecimal installFee;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

    /** 参考图片文件ID（file_info.id），整体替换，最多 10 张。 */
    @Size(max = HomePurchaseConstant.MAX_IMAGE_COUNT, message = "参考图片最多 10 张")
    private List<Long> fileIds;
}
