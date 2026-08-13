package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 通知公告查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeQueryParam extends PageQueryParam {

    /** 主键。 */
    private Long noticeId;

    /** 公告标题。 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告）。 */
    private String noticeType;

    /** 公告内容。 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭）。 */
    private String status;

    /** 备注。 */
    private String remark;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
