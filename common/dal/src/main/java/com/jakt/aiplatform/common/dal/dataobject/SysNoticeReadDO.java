package com.jakt.aiplatform.common.dal.dataobject;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告已读记录 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeReadDO extends BaseDO {
    /** 主键。 */
    private Long readId;

    /** 公告id。 */
    private Integer noticeId;

    /** 用户id。 */
    private Long userId;

    /** 阅读时间。 */
    private LocalDateTime readTime;

}
