package com.jakt.aiplatform.web.result;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
/**
 * 菜单响应（含子菜单树）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthMenuResponse extends BaseResult {
    /** 菜单ID。 */
    private Long menuId;
    /** 菜单名称。 */
    private String menuName;
    /** 父菜单ID。 */
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
    /** 图标。 */
    private String icon;
    /** 子菜单。 */
    private List<AuthMenuResponse> children;
    /** 创建时间。 */
    private LocalDateTime createTime;
}
