package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.common.framework.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页留言板留言领域模型：匿名身份（头像/昵称/配色）由客户端 IP 决定。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeMessage extends BaseModel {

    /** 主键。 */
    private Long id;

    /** 留言内容。 */
    private String content;

    /** 客户端IP（仅用于判定「我的留言」，不返回前端）。 */
    private String clientIp;

    /** 头像文件ID（file_info.id）。 */
    private Long avatarFileId;

    /** 匿名昵称（动物名）。 */
    private String avatarName;

    /** 气泡配色下标。 */
    private Integer colorIndex;

    /** 创建者（登录用户名，仅审计）。 */
    private String createBy;
}
