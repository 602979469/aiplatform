package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新公告已读记录请求 DTO。
 *
 * <p>校验规则与 sys_notice_read 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeReadUpdateRequest extends BaseRequest {
    /** 公告id。 */
    @NotNull(message = "公告id不能为空")
    private Long noticeId;

    /** 用户id。 */
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /** 阅读时间。 */
    @NotNull(message = "阅读时间不能为空")
    private LocalDateTime readTime;

}
