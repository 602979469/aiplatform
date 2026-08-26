package com.jakt.aiplatform.core.model.domain;
import com.jakt.aiplatform.common.framework.model.BaseModel;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限表领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthMenu extends BaseModel {
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

    /** 是否外链（0是 1否）。 */
    private String isFrame;

    /** 类型（M目录 C菜单 F按钮）。 */
    private MenuTypeEnum menuType;

    /** 是否显示（0显示 1隐藏）。 */
    private VisibleEnum visible;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 权限标识。 */
    private String perms;

    /** 菜单图标。 */
    private String icon;

    /** 备注。 */
    private String remark;

    /** 子菜单（树组装用，不入库）。 */
    private List<AuthMenu> children;

}
