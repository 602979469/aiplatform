package com.jakt.aiplatform.core.repository.convertor;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthMenuDO;
import com.jakt.aiplatform.common.dal.query.AuthMenuDalQuery;
import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;


/**
 * 菜单权限表 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AuthMenuConvertor {

    private AuthMenuConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param authMenuDO 菜单权限表数据对象；为空返回 null
     * @return 菜单权限表领域模型
     */
    public static AuthMenu toModel(AuthMenuDO source) {
        if (source == null) {
            return null;
        }
        AuthMenu target = new AuthMenu();
        target.setMenuId(source.getMenuId());
        target.setMenuName(source.getMenuName());
        target.setParentId(source.getParentId());
        target.setOrderNum(source.getOrderNum());
        target.setPath(source.getPath());
        target.setComponent(source.getComponent());
        target.setMenuType(BaseEnum.fromCode(MenuTypeEnum.class, source.getMenuType()));
        target.setVisible(BaseEnum.fromCode(VisibleEnum.class, source.getVisible()));
        target.setStatus(BaseEnum.fromCode(EnableStatusEnum.class, source.getStatus()));
        target.setPerms(source.getPerms());
        target.setIcon(source.getIcon());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param authMenu 菜单权限表领域模型
     * @return 菜单权限表数据对象
     */
    public static AuthMenuDO toDO(AuthMenu source) {
        if (source == null) {
            return null;
        }
        AuthMenuDO target = new AuthMenuDO();
        target.setMenuId(source.getMenuId());
        target.setMenuName(source.getMenuName());
        target.setParentId(source.getParentId());
        target.setOrderNum(source.getOrderNum());
        target.setPath(source.getPath());
        target.setComponent(source.getComponent());
        target.setMenuType(ObjectUtil.isNull(source.getMenuType()) ? null : source.getMenuType().getCode());
        target.setVisible(ObjectUtil.isNull(source.getVisible()) ? null : source.getVisible().getCode());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setPerms(source.getPerms());
        target.setIcon(source.getIcon());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    public static AuthMenuDalQuery toDalQuery(AuthMenuQueryParam source) {
        AuthMenuDalQuery target = new AuthMenuDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setMenuId(source.getMenuId());
        target.setMenuName(source.getMenuName());
        target.setParentId(source.getParentId());
        target.setOrderNum(source.getOrderNum());
        target.setPath(source.getPath());
        target.setComponent(source.getComponent());
        target.setMenuType(ObjectUtil.isNull(source.getMenuType()) ? null : source.getMenuType().getCode());
        target.setVisible(source.getVisible());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setPerms(source.getPerms());
        target.setIcon(source.getIcon());
        target.setRemark(source.getRemark());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
