package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 公告已读记录查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeReadQueryParam extends PageParam {

    /** 主键。 */
    private Long readId;

    /** 公告id。 */
    private Integer noticeId;

    /** 用户id。 */
    private Long userId;

    /** 阅读时间。 */
    private LocalDateTime readTime;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
