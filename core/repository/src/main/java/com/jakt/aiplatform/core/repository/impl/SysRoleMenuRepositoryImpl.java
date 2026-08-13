package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleMenuDO;
import com.jakt.aiplatform.common.dal.mapper.SysRoleMenuMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysRoleMenu;
import com.jakt.aiplatform.core.repository.SysRoleMenuRepository;
import com.jakt.aiplatform.core.repository.convertor.SysRoleMenuConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色菜单关联仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysRoleMenuRepositoryImpl implements SysRoleMenuRepository {

    /** 角色菜单关联 Mapper。 */
    private final SysRoleMenuMapper sysRoleMenuMapper;

    public SysRoleMenuRepositoryImpl(SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    @Override
    public int deleteRoleMenuByRoleId(Long roleId) {
        return sysRoleMenuMapper.deleteRoleMenuByRoleId(roleId);
    }

    @Override
    public int selectCountRoleMenuByMenuId(Long menuId) {
        return sysRoleMenuMapper.selectCountRoleMenuByMenuId(menuId);
    }

    @Override
    public int deleteRoleMenu(Long[] ids) {
        return sysRoleMenuMapper.deleteRoleMenu(ids);
    }

    @Override
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList) {
        List<SysRoleMenuDO> doList = ListUtil.convert(roleMenuList, SysRoleMenuConvertor::toDO);
        return sysRoleMenuMapper.batchRoleMenu(doList);
    }
}
