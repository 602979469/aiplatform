package com.jakt.aiplatform.core.model.domain;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告已读记录领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeRead extends BaseModel {
    /** 主键。 */
    private Long readId;

    /** 公告id。 */
    private Long noticeId;

    /** 用户id。 */
    private Long userId;

    /** 阅读时间。 */
    private LocalDateTime readTime;

}
