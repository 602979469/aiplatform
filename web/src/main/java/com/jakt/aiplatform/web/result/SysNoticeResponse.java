package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.NoticeTypeEnum;
import com.jakt.aiplatform.core.model.enums.NoticeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 通知公告响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysNoticeResponse extends BaseResult {
    /** 主键。 */
    private Long noticeId;

    /** 公告标题。 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告）。 */
    private NoticeTypeEnum noticeType;

    /** 公告内容。 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭）。 */
    private NoticeStatusEnum status;

    /** 备注。 */
    private String remark;

}
