package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.jakt.aiplatform.core.model.enums.NoticeTypeEnum;
import com.jakt.aiplatform.core.model.enums.NoticeStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新通知公告请求 DTO。
 *
 * <p>校验规则与 sys_notice 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeUpdateRequest extends BaseRequest {
    /** 公告标题。 */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题长度不能超过 50")
    private String noticeTitle;

    /** 公告类型（1通知 2公告）。 */
    @NotNull(message = "公告类型（1通知 2公告）不能为空")
    private NoticeTypeEnum noticeType;

    /** 公告内容。 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭）。 */
    private NoticeStatusEnum status;

    /** 备注。 */
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;

}
