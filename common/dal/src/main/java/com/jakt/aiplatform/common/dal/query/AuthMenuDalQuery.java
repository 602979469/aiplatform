package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthMenuDalQuery extends DalPageQuery {

    private Long menuId;

    private String menuName;

    private Long parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String menuType;

    private String visible;

    private String status;

    private String perms;

    private String icon;

    private String remark;

    private LocalDateTime createTimeBegin;

    private LocalDateTime createTimeEnd;

    private LocalDateTime updateTimeBegin;

    private LocalDateTime updateTimeEnd;
}
