package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.VisibleEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseModel {
    /** 主键。 */
    private Long menuId;

    /** 菜单名称。 */
    private String menuName;

    /** 父菜单ID。 */
    private Long parentId;

    /** 显示顺序。 */
    private String orderNum;

    /** 请求地址。 */
    private String url;

    /** 打开方式（menuItem页签 menuBlank新窗口）。 */
    private String target;

    /** 菜单类型（M目录 C菜单 F按钮）。 */
    private String menuType;

    /** 菜单状态（0显示 1隐藏）。 */
    private VisibleEnum visible;

    /** 是否刷新（0刷新 1不刷新）。 */
    private String isRefresh;

    /** 权限标识。 */
    private String perms;

    /** 菜单图标。 */
    private String icon;

    /** 备注。 */
    private String remark;

}
