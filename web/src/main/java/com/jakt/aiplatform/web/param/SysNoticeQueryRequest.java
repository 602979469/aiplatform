package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeQueryRequest extends BaseRequest {

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

    /** 页码，从 1 开始。 */
    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    /** 每页条数。 */
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Integer pageSize = 10;
}
