package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页留言板留言 DO 对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeMessageDO extends BaseDO {

    /** 主键ID。 */
    private Long id;

    /** 留言内容。 */
    private String content;

    /** 客户端IP（匿名身份归属：同一 IP 同色同头像；仅审计，不返回前端）。 */
    private String clientIp;

    /** 头像文件ID（file_info.id，namespace=avatar）。 */
    private Long avatarFileId;

    /** 匿名昵称（动物名）。 */
    private String avatarName;

    /** 气泡配色下标（同一 IP 恒定）。 */
    private Integer colorIndex;

    /** 创建者（登录用户名，仅审计）。 */
    private String createBy;
}
