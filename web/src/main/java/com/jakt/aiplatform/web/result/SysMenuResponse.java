package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.VisibleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 菜单响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysMenuResponse extends BaseResult {
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
