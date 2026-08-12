package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysMenuDO;
import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
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
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
