package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新增/修改菜单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthMenuRequest extends BaseRequest {

    /** 菜单ID（修改时必填）。 */
    private Long menuId;

    /** 菜单名称。 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50")
    private String menuName;

    /** 父菜单ID（0 为根）。 */
    private Long parentId;

    /** 显示顺序。 */
    private Integer orderNum;

    /** 路由地址。 */
    @Size(max = 200, message = "路由地址长度不能超过200")
    private String path;

    /** 组件路径。 */
    @Size(max = 200, message = "组件路径长度不能超过200")
    private String component;

    /** 是否外链（0是 1否）。 */
    private String isFrame;

    /** 类型（M目录 C菜单 F按钮）。 */
    @NotNull(message = "菜单类型不能为空")
    private MenuTypeEnum menuType;

    /** 是否显示（0显示 1隐藏）。 */
    private VisibleEnum visible;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 权限标识。 */
    @Size(max = 100, message = "权限标识长度不能超过100")
    private String perms;

    /** 图标。 */
    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}
