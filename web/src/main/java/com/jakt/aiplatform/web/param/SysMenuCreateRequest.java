package com.jakt.aiplatform.web.param;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建菜单请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuCreateRequest extends BaseRequest {

    /** 菜单名称。 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过 50")
    private String menuName;

    /** 父菜单ID。 */
    private Long parentId;

    /** 显示顺序。 */
    private String orderNum;

    /** 请求地址。 */
    @Size(max = 200, message = "请求地址长度不能超过 200")
    private String url;

    /** 打开方式（menuItem页签 menuBlank新窗口）。 */
    @Size(max = 20, message = "打开方式（menuItem页签 menuBlank新窗口）长度不能超过 20")
    private String target;

    /** 菜单类型（M目录 C菜单 F按钮）。 */
    @Size(max = 1, message = "菜单类型（M目录 C菜单 F按钮）长度不能超过 1")
    private String menuType;

    /** 菜单状态（0显示 1隐藏）。 */
    private VisibleEnum visible;

    /** 是否刷新（0刷新 1不刷新）。 */
    @Size(max = 1, message = "是否刷新（0刷新 1不刷新）长度不能超过 1")
    private String isRefresh;

    /** 权限标识。 */
    @Size(max = 100, message = "权限标识长度不能超过 100")
    private String perms;

    /** 菜单图标。 */
    @Size(max = 100, message = "菜单图标长度不能超过 100")
    private String icon;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
