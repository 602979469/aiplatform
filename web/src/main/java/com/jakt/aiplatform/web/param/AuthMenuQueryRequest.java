package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单列表查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthMenuQueryRequest extends BaseRequest {

    /** 菜单名称。 */
    private String menuName;

    /** 菜单类型。 */
    private MenuTypeEnum menuType;

    /** 状态。 */
    private EnableStatusEnum status;
}
