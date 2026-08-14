package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthMenuDO;
import com.jakt.aiplatform.common.dal.mapper.AuthMenuMapper;
import com.jakt.aiplatform.common.dal.mapper.AuthRoleMenuMapper;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.AuthMenuRepository;
import com.jakt.aiplatform.core.repository.convertor.AuthMenuConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单权限表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class AuthMenuRepositoryImpl implements AuthMenuRepository {

    /** 菜单权限表 Mapper。 */
    private final AuthMenuMapper authMenuMapper;

    /** 角色菜单关联表 Mapper。 */
    private final AuthRoleMenuMapper authRoleMenuMapper;

    public AuthMenuRepositoryImpl(AuthMenuMapper authMenuMapper, AuthRoleMenuMapper authRoleMenuMapper) {
        this.authMenuMapper = authMenuMapper;
        this.authRoleMenuMapper = authRoleMenuMapper;
    }

    @Override
    public AuthMenu findById(Long id) {
        AuthMenuDO menuDO = authMenuMapper.selectById(id);
        return AuthMenuConvertor.toModel(menuDO);
    }

    @Override
    public List<AuthMenu> findList(AuthMenuQueryParam query) {
        List<AuthMenuDO> sourceList = authMenuMapper.selectList(AuthMenuConvertor.toDalQuery(query));
        return ConvertUtil.map(sourceList, AuthMenuConvertor::toModel);
    }

    @Override
    public AuthMenu findOne(AuthMenuQueryParam query) {
        List<AuthMenuDO> doList = authMenuMapper.selectList(AuthMenuConvertor.toDalQuery(query));
        AssertUtil.throwErrWhenTrue(doList.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE,
                "查询结果不唯一：预期 1 条，实际 " + doList.size() + " 条");
        return doList.isEmpty() ? null : AuthMenuConvertor.toModel(doList.get(0));
    }

    @Override
    public PageResult<AuthMenu> findPage(AuthMenuQueryParam query) {
        com.jakt.aiplatform.common.dal.query.AuthMenuDalQuery dalQuery = AuthMenuConvertor.toDalQuery(query);
        List<AuthMenuDO> doList = authMenuMapper.selectPage(dalQuery);
        long total = authMenuMapper.countByQuery(dalQuery);
        List<AuthMenu> list = ConvertUtil.map(doList, AuthMenuConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public AuthMenu insert(AuthMenu authMenu) {
        AuthMenuDO authMenuDO = AuthMenuConvertor.toDO(authMenu);
        authMenuMapper.insert(authMenuDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        authMenu.setMenuId(authMenuDO.getMenuId());
        return authMenu;
    }

    @Override
    public int update(AuthMenu authMenu) {
        return authMenuMapper.update(AuthMenuConvertor.toDO(authMenu));
    }

    @Override
    public int updateByCondition(AuthMenu authMenu) {
        return authMenuMapper.updateByCondition(AuthMenuConvertor.toDO(authMenu));
    }

    @Override
    public int deleteById(Long id) {
        return authMenuMapper.deleteById(id);
    }

    @Override
    public List<AuthMenu> findMenusByUserId(Long userId) {
        List<AuthMenuDO> sourceList = authMenuMapper.selectMenusByUserId(userId);
        return ConvertUtil.map(sourceList, AuthMenuConvertor::toModel);
    }

    @Override
    public List<String> findPermsByUserId(Long userId) {
        return authMenuMapper.selectPermsByUserId(userId);
    }

    @Override
    public void clearMenuBindings(Long menuId) {
        authRoleMenuMapper.deleteByMenuId(menuId);
    }
}
