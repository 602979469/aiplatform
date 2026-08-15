package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import cn.hutool.core.collection.CollUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthRoleMenuDO;
import com.jakt.aiplatform.common.dal.dataobject.AuthRoleDO;
import com.jakt.aiplatform.common.dal.mapper.AuthRoleMapper;
import com.jakt.aiplatform.common.dal.mapper.AuthRoleMenuMapper;
import com.jakt.aiplatform.common.dal.mapper.AuthUserRoleMapper;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.AuthRoleRepository;
import com.jakt.aiplatform.core.repository.convertor.AuthRoleConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class AuthRoleRepositoryImpl implements AuthRoleRepository {

    /** 角色表 Mapper。 */
    private final AuthRoleMapper authRoleMapper;

    /** 角色菜单关联表 Mapper。 */
    private final AuthRoleMenuMapper authRoleMenuMapper;

    /** 用户角色关联表 Mapper。 */
    private final AuthUserRoleMapper authUserRoleMapper;

    public AuthRoleRepositoryImpl(AuthRoleMapper authRoleMapper,
                                  AuthRoleMenuMapper authRoleMenuMapper,
                                  AuthUserRoleMapper authUserRoleMapper) {
        this.authRoleMapper = authRoleMapper;
        this.authRoleMenuMapper = authRoleMenuMapper;
        this.authUserRoleMapper = authUserRoleMapper;
    }

    @Override
    public AuthRole findById(Long id) {
        AuthRoleDO roleDO = authRoleMapper.selectById(id);
        return AuthRoleConvertor.toModel(roleDO);
    }

    @Override
    public List<String> findRoleKeysByUserId(Long userId) {
        return authRoleMapper.selectRoleKeysByUserId(userId);
    }

    @Override
    public List<AuthRole> findList(AuthRoleQueryParam query) {
        List<AuthRoleDO> sourceList = authRoleMapper.selectList(AuthRoleConvertor.toDalQuery(query));
        return ConvertUtil.map(sourceList, AuthRoleConvertor::toModel);
    }

    @Override
    public AuthRole findOne(AuthRoleQueryParam query) {
        List<AuthRoleDO> doList = authRoleMapper.selectList(AuthRoleConvertor.toDalQuery(query));
        AssertUtil.throwErrWhenTrue(doList.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE,
                "查询结果不唯一：预期 1 条，实际 " + doList.size() + " 条");
        return doList.isEmpty() ? null : AuthRoleConvertor.toModel(doList.get(0));
    }

    @Override
    public PageResult<AuthRole> findPage(AuthRoleQueryParam query) {
        com.jakt.aiplatform.common.dal.query.AuthRoleDalQuery dalQuery = AuthRoleConvertor.toDalQuery(query);
        List<AuthRoleDO> doList = authRoleMapper.selectPage(dalQuery);
        long total = authRoleMapper.countByQuery(dalQuery);
        List<AuthRole> list = ConvertUtil.map(doList, AuthRoleConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public AuthRole insert(AuthRole authRole) {
        AuthRoleDO authRoleDO = AuthRoleConvertor.toDO(authRole);
        authRoleMapper.insert(authRoleDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        authRole.setRoleId(authRoleDO.getRoleId());
        return authRole;
    }

    @Override
    public int update(AuthRole authRole) {
        return authRoleMapper.update(AuthRoleConvertor.toDO(authRole));
    }

    @Override
    public int updateByCondition(AuthRole authRole) {
        return authRoleMapper.updateByCondition(AuthRoleConvertor.toDO(authRole));
    }

    @Override
    public int deleteById(Long id) {
        return authRoleMapper.deleteById(id);
    }

    @Override
    public List<Long> findMenuIdsByRoleId(Long roleId) {
        return authRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public void replaceRoleMenus(Long roleId, List<Long> menuIds) {
        authRoleMenuMapper.deleteByRoleId(roleId);
        if (CollUtil.isNotEmpty(menuIds)) {
            List<AuthRoleMenuDO> list = menuIds.stream().distinct()
                    .map(menuId -> buildRoleMenu(roleId, menuId)).toList();
            authRoleMenuMapper.batchInsert(list);
        }
    }

    @Override
    public void clearRoleBindings(Long roleId) {
        authRoleMenuMapper.deleteByRoleId(roleId);
        authUserRoleMapper.deleteByRoleId(roleId);
    }

    /** 组装角色菜单关联对象。 */
    private AuthRoleMenuDO buildRoleMenu(Long roleId, Long menuId) {
        AuthRoleMenuDO roleMenu = new AuthRoleMenuDO();
        roleMenu.setRoleId(roleId);
        roleMenu.setMenuId(menuId);
        return roleMenu;
    }
}
