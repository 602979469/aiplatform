package com.jakt.aiplatform.web.result;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
/**
 * 前端路由响应（侧边菜单）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MenuRouteResponse extends BaseResult {
    /** 菜单ID。 */
    private Long menuId;
    /** 菜单名称。 */
    private String menuName;
    /** 路由地址。 */
    private String path;
    /** 组件路径。 */
    private String component;
    /** 是否外链（0是 1否）。 */
    private String isFrame;
    /** 类型（M目录 C菜单）。 */
    private MenuTypeEnum menuType;
    /** 图标。 */
    private String icon;
    /** 子路由。 */
    private List<MenuRouteResponse> children;
}
