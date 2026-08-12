package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.NoticeTypeEnum;
import com.jakt.aiplatform.core.model.enums.NoticeStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNotice extends BaseModel {
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
