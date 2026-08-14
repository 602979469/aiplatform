package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限表 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthMenuDO extends BaseDO {
    /** 主键。 */
    private Long menuId;

    /** 菜单名称。 */
    private String menuName;

    /** 父菜单ID（0 为根）。 */
    private Long parentId;

    /** 显示顺序。 */
    private Integer orderNum;

    /** 路由地址。 */
    private String path;

    /** 组件路径。 */
    private String component;

    /** 类型（M目录 C菜单 F按钮）。 */
    private String menuType;

    /** 是否显示（0显示 1隐藏）。 */
    private String visible;

    /** 状态（0正常 1停用）。 */
    private String status;

    /** 权限标识。 */
    private String perms;

    /** 菜单图标。 */
    private String icon;

    /** 备注。 */
    private String remark;

}
