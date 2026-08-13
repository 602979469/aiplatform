package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysMenuDO;
import com.jakt.aiplatform.common.dal.mapper.SysMenuMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import com.jakt.aiplatform.core.repository.SysMenuRepository;
import com.jakt.aiplatform.core.repository.convertor.SysMenuConvertor;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysMenuRepositoryImpl implements SysMenuRepository {

    /** 菜单 Mapper。 */
    private final SysMenuMapper sysMenuMapper;

    public SysMenuRepositoryImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public List<SysMenu> selectMenuAll() {
        List<SysMenuDO> list = sysMenuMapper.selectMenuAll();
        return ListUtil.convert(list, SysMenuConvertor::toModel);
    }

    @Override
    public List<SysMenu> selectMenuAllByUserId(Long userId) {
        List<SysMenuDO> list = sysMenuMapper.selectMenuAllByUserId(userId);
        return ListUtil.convert(list, SysMenuConvertor::toModel);
    }

    @Override
    public List<SysMenu> selectMenuNormalAll() {
        List<SysMenuDO> list = sysMenuMapper.selectMenuNormalAll();
        return ListUtil.convert(list, SysMenuConvertor::toModel);
    }

    @Override
    public List<SysMenu> selectMenusByUserId(Long userId) {
        List<SysMenuDO> list = sysMenuMapper.selectMenusByUserId(userId);
        return ListUtil.convert(list, SysMenuConvertor::toModel);
    }

    @Override
    public List<String> selectPermsByUserId(Long userId) {
        return sysMenuMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<String> selectPermsByRoleId(Long roleId) {
        return sysMenuMapper.selectPermsByRoleId(roleId);
    }

    @Override
    public List<String> selectMenuTree(Long roleId) {
        return sysMenuMapper.selectMenuTree(roleId);
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        List<SysMenuDO> list = sysMenuMapper.selectMenuList(SysMenuConvertor.toQueryParam(menu));
        return ListUtil.convert(list, SysMenuConvertor::toModel);
    }

    @Override
    public List<SysMenu> selectMenuListByUserId(SysMenu menu) {
        SysMenuQueryParam query = SysMenuConvertor.toQueryParam(menu);
        query.setParams(menu.getParams());
        List<SysMenuDO> list = sysMenuMapper.selectMenuListByUserId(query);
        return ListUtil.convert(list, SysMenuConvertor::toModel);
    }

    @Override
    public int deleteMenuById(Long menuId) {
        return sysMenuMapper.deleteMenuById(menuId);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return SysMenuConvertor.toModel(sysMenuMapper.selectMenuById(menuId));
    }

    @Override
    public int selectCountMenuByParentId(Long parentId) {
        return sysMenuMapper.selectCountMenuByParentId(parentId);
    }

    @Override
    public int insertMenu(SysMenu menu) {
        return sysMenuMapper.insert(SysMenuConvertor.toDO(menu));
    }

    @Override
    public int updateMenu(SysMenu menu) {
        return sysMenuMapper.update(SysMenuConvertor.toDO(menu));
    }

    @Override
    public int updateMenuSort(SysMenu menu) {
        return sysMenuMapper.updateMenuSort(SysMenuConvertor.toDO(menu));
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        SysMenuDO target = sysMenuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (target == null) {
            return true;
        }
        return ObjectUtil.equal(target.getMenuId(), menu.getMenuId());
    }
}
