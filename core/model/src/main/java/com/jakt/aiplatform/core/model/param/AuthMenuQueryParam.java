package com.jakt.aiplatform.core.model.param;
import com.jakt.aiplatform.common.framework.param.PageParam;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 菜单权限表查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthMenuQueryParam extends PageParam {

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
    private MenuTypeEnum menuType;

    /** 是否显示（0显示 1隐藏）。 */
    private String visible;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 权限标识。 */
    private String perms;

    /** 菜单图标。 */
    private String icon;

    /** 备注。 */
    private String remark;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
