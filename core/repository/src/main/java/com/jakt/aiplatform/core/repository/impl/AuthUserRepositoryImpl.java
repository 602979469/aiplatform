package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import cn.hutool.core.collection.CollUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthUserDO;
import com.jakt.aiplatform.common.dal.dataobject.AuthUserRoleDO;
import com.jakt.aiplatform.common.dal.mapper.AuthUserMapper;
import com.jakt.aiplatform.common.dal.mapper.AuthUserRoleMapper;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.AuthUserRepository;
import com.jakt.aiplatform.core.repository.convertor.AuthUserConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class AuthUserRepositoryImpl implements AuthUserRepository {

    /** 用户表 Mapper。 */
    private final AuthUserMapper authUserMapper;

    /** 用户角色关联表 Mapper。 */
    private final AuthUserRoleMapper authUserRoleMapper;

    public AuthUserRepositoryImpl(AuthUserMapper authUserMapper, AuthUserRoleMapper authUserRoleMapper) {
        this.authUserMapper = authUserMapper;
        this.authUserRoleMapper = authUserRoleMapper;
    }

    @Override
    public AuthUser findById(Long id) {
        AuthUserDO userDO = authUserMapper.selectById(id);
        return AuthUserConvertor.toModel(userDO);
    }

    @Override
    public AuthUser findByUsername(String username) {
        AuthUserDO userDO = authUserMapper.selectByUsername(username);
        return AuthUserConvertor.toModel(userDO);
    }

    @Override
    public List<AuthUser> findList(AuthUserQueryParam query) {
        List<AuthUserDO> sourceList = authUserMapper.selectList(AuthUserConvertor.toDalQuery(query));
        return ConvertUtil.map(sourceList, AuthUserConvertor::toModel);
    }

    @Override
    public AuthUser findOne(AuthUserQueryParam query) {
        List<AuthUserDO> doList = authUserMapper.selectList(AuthUserConvertor.toDalQuery(query));
        AssertUtil.throwErrWhenTrue(doList.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE,
                "查询结果不唯一：预期 1 条，实际 " + doList.size() + " 条");
        return doList.isEmpty() ? null : AuthUserConvertor.toModel(doList.get(0));
    }

    @Override
    public PageResult<AuthUser> findPage(AuthUserQueryParam query) {
        com.jakt.aiplatform.common.dal.query.AuthUserDalQuery dalQuery = AuthUserConvertor.toDalQuery(query);
        List<AuthUserDO> doList = authUserMapper.selectPage(dalQuery);
        long total = authUserMapper.countByQuery(dalQuery);
        List<AuthUser> list = ConvertUtil.map(doList, AuthUserConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public AuthUser insert(AuthUser authUser) {
        AuthUserDO authUserDO = AuthUserConvertor.toDO(authUser);
        authUserMapper.insert(authUserDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        authUser.setUserId(authUserDO.getUserId());
        return authUser;
    }

    @Override
    public int update(AuthUser authUser) {
        return authUserMapper.update(AuthUserConvertor.toDO(authUser));
    }

    @Override
    public int updateByCondition(AuthUser authUser) {
        return authUserMapper.updateByCondition(AuthUserConvertor.toDO(authUser));
    }

    @Override
    public int deleteById(Long id) {
        return authUserMapper.deleteById(id);
    }

    @Override
    public void replaceRoles(Long userId, List<Long> roleIds) {
        authUserRoleMapper.deleteByUserId(userId);
        if (CollUtil.isNotEmpty(roleIds)) {
            List<AuthUserRoleDO> list = roleIds.stream().distinct()
                    .map(roleId -> buildUserRole(userId, roleId)).toList();
            authUserRoleMapper.batchInsert(list);
        }
    }

    @Override
    public List<Long> findRoleIdsByUserId(Long userId) {
        return authUserRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public void clearUserRoles(Long userId) {
        authUserRoleMapper.deleteByUserId(userId);
    }

    /** 组装用户角色关联对象。 */
    private AuthUserRoleDO buildUserRole(Long userId, Long roleId) {
        AuthUserRoleDO userRole = new AuthUserRoleDO();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRole;
    }
}
