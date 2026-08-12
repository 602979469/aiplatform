package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysNoticeDO extends BaseDO {
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

}
