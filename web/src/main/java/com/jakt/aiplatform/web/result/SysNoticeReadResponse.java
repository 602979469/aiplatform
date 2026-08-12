package com.jakt.aiplatform.web.result;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 公告已读记录响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysNoticeReadResponse extends BaseResult {
    /** 主键。 */
    private Long readId;

    /** 公告id。 */
    private Long noticeId;

    /** 用户id。 */
    private Long userId;

    /** 阅读时间。 */
    private LocalDateTime readTime;

}
