package com.jakt.aiplatform.web.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 留言板留言响应：只暴露匿名身份，不含客户端 IP。
 */
@Data
public class HomeMessageResponse implements Serializable {

    /** 序列化版本号。 */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键。 */
    private Long id;

    /** 留言内容。 */
    private String content;

    /** 头像地址（相对路径，前端拼接 VUE_APP_BASE_API）。 */
    private String avatarUrl;

    /** 匿名昵称（动物名）。 */
    private String avatarName;

    /** 气泡配色下标（0 ~ 11，与前端调色板对应）。 */
    private Integer colorIndex;

    /** 是否当前请求者本人（同一 IP）发的留言。 */
    private Boolean mine;

    /** 留言时间（毫秒时间戳，前端换算相对时间）。 */
    private Long createTimestamp;
}
