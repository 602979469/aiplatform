package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.NoticeStatusEnum;
import com.jakt.aiplatform.core.model.enums.NoticeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告领域模型（RuoYi 结构：继承 BaseEntity，含组装字段 isRead）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNotice extends BaseEntity {

    /** 公告ID。 */
    private Long noticeId;

    /** 公告标题。 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告）。 */
    private NoticeTypeEnum noticeType;

    /** 公告内容。 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭）。 */
    private NoticeStatusEnum status;

    /** 是否已读（组装字段）。 */
    private boolean isRead;
}
