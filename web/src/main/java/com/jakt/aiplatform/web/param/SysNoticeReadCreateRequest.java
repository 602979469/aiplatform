package com.jakt.aiplatform.web.param;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建公告已读记录请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeReadCreateRequest extends BaseRequest {

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
