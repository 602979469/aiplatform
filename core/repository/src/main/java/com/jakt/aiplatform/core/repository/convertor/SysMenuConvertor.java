package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysMenuDO;
import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;

/**
 * 菜单 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysMenuConvertor {

    private SysMenuConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code、String 排序转 Integer，显式赋值）。
     *
     * @param menu 菜单领域模型
     * @return 菜单查询参数
     */
    public static SysMenuQueryParam toQueryParam(SysMenu menu) {
        SysMenuQueryParam query = new SysMenuQueryParam();
        query.setMenuId(menu.getMenuId());
        query.setMenuName(menu.getMenuName());
        query.setParentId(menu.getParentId());
        query.setOrderNum(menu.getOrderNum() == null ? null : Convert.toInt(menu.getOrderNum()));
        query.setUrl(menu.getUrl());
        query.setTarget(menu.getTarget());
        query.setMenuType(menu.getMenuType());
        query.setVisible(menu.getVisible() == null ? null : menu.getVisible().getCode());
        query.setIsRefresh(menu.getIsRefresh());
        query.setPerms(menu.getPerms());
        query.setIcon(menu.getIcon());
        query.setRemark(menu.getRemark());
        query.setParams(menu.getParams());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 菜单数据对象（条件载体）
     * @return 菜单查询参数
     */
    public static SysMenuQueryParam toQueryParam(SysMenuDO condition) {
        SysMenuQueryParam query = new SysMenuQueryParam();
        query.setMenuId(condition.getMenuId());
        query.setMenuName(condition.getMenuName());
        query.setParentId(condition.getParentId());
        query.setOrderNum(condition.getOrderNum());
        query.setUrl(condition.getUrl());
        query.setTarget(condition.getTarget());
        query.setMenuType(condition.getMenuType());
        query.setVisible(condition.getVisible());
        query.setIsRefresh(condition.getIsRefresh());
        query.setPerms(condition.getPerms());
        query.setIcon(condition.getIcon());
        query.setRemark(condition.getRemark());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysMenuDO 菜单数据对象；为空返回 null
     * @return 菜单领域模型
     */
    public static SysMenu toModel(SysMenuDO source) {
        if (source == null) {
            return null;
        }
        SysMenu target = new SysMenu();
        target.setMenuId(source.getMenuId());
        target.setMenuName(source.getMenuName());
        target.setParentName(source.getParentName());
        target.setParentId(source.getParentId());
        target.setOrderNum(Convert.toStr(source.getOrderNum()));
        target.setUrl(source.getUrl());
        target.setTarget(source.getTarget());
        target.setMenuType(source.getMenuType());
        target.setVisible(VisibleEnum.fromCode(source.getVisible()));
        target.setIsRefresh(source.getIsRefresh());
        target.setPerms(source.getPerms());
        target.setIcon(source.getIcon());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysMenu 菜单领域模型
     * @return 菜单数据对象
     */
    public static SysMenuDO toDO(SysMenu source) {
        SysMenuDO target = new SysMenuDO();
        target.setMenuId(source.getMenuId());
        target.setMenuName(source.getMenuName());
        target.setParentId(source.getParentId());
        target.setOrderNum(Convert.toInt(source.getOrderNum()));
        target.setUrl(source.getUrl());
        target.setTarget(source.getTarget());
        target.setMenuType(source.getMenuType());
        target.setVisible(ObjectUtil.isNull(source.getVisible()) ? null : source.getVisible().getCode());
        target.setIsRefresh(source.getIsRefresh());
        target.setPerms(source.getPerms());
        target.setIcon(source.getIcon());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
