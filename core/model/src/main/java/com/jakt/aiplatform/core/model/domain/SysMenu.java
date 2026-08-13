package com.jakt.aiplatform.core.model.domain;

import java.util.ArrayList;
import java.util.List;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单领域模型（RuoYi 结构：继承 BaseEntity，含组装字段 parentName/children）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {

    /** 菜单ID。 */
    private Long menuId;

    /** 菜单名称。 */
    private String menuName;

    /** 父菜单名称（组装字段）。 */
    private String parentName;

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

    /** 子菜单（组装字段）。 */
    private List<SysMenu> children = new ArrayList<>();
}
