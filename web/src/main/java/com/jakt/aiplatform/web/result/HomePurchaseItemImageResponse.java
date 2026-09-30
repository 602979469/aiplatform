package com.jakt.aiplatform.web.result;

import lombok.Data;

/**
 * 采购项参考图片响应 DTO：只透出文件ID，下载地址由前端按文件接口拼接。
 */
@Data
public class HomePurchaseItemImageResponse {

    /** 图片记录主键。 */
    private Long id;

    /** 文件ID（file_info.id）。 */
    private Long fileId;

    /** 展示顺序（0 起）。 */
    private Integer orderNum;
}
