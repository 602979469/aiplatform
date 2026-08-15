package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.exception.AiPlatformException;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.common.util.result.Result;
import com.jakt.aiplatform.common.util.template.BizTemplate;
import com.jakt.aiplatform.common.util.template.TransactionTemplate;
import com.jakt.aiplatform.core.repository.AuthRoleRepository;
import com.jakt.aiplatform.core.service.AuthRoleAdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 角色管理领域服务实现：跨表多写统一走 BizTemplate。
 */
@Service
public class AuthRoleAdminServiceImpl implements AuthRoleAdminService {

    private final AuthRoleRepository authRoleRepository;

    private final TransactionTemplate transactionTemplate;

    public AuthRoleAdminServiceImpl(AuthRoleRepository authRoleRepository,
                                    TransactionTemplate transactionTemplate) {
        this.authRoleRepository = authRoleRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public PageResult<AuthRole> pageRole(AuthRoleQueryParam query) {
        return authRoleRepository.findPage(query);
    }

    @Override
    public AuthRole getRole(Long roleId) {
        AuthRole role = authRoleRepository.findById(roleId);
        AssertUtil.throwErrWhenNull(role, ErrorCodeEnum.RESOURCE_NOT_FOUND, "角色不存在");
        return role;
    }

    @Override
    public AuthRole createRole(AuthRole role) {
        checkRoleKeyUnique(role.getRoleKey(), null);
        role.setRoleSort(role.getRoleSort() == null ? 0 : role.getRoleSort());
        if (role.getStatus() == null) {
            role.setStatus(EnableStatusEnum.ENABLE);
        }
        authRoleRepository.insert(role);
        return role;
    }

    @Override
    public void updateRole(AuthRole role) {
        getRole(role.getRoleId());
        checkRoleKeyUnique(role.getRoleKey(), role.getRoleId());
        int affected = authRoleRepository.updateByCondition(role);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void changeRoleStatus(Long roleId, EnableStatusEnum status) {
        AuthRole update = new AuthRole();
        update.setRoleId(roleId);
        update.setStatus(status);
        int affected = authRoleRepository.updateByCondition(update);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        getRole(roleId);
        checkResult(BizTemplate.executeWithoutResult(transactionTemplate,
                () -> authRoleRepository.replaceRoleMenus(roleId, menuIds)));
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return authRoleRepository.findMenuIdsByRoleId(roleId);
    }

    @Override
    public void deleteRole(Long roleId) {
        getRole(roleId);
        checkResult(BizTemplate.executeWithoutResult(transactionTemplate,
                () -> {
                    authRoleRepository.clearRoleBindings(roleId);
                    authRoleRepository.deleteById(roleId);
                }));
    }

    /** 校验角色标识唯一（可排除自身）。 */
    private void checkRoleKeyUnique(String roleKey, Long excludeRoleId) {
        AuthRoleQueryParam query = new AuthRoleQueryParam();
        query.setRoleKey(roleKey);
        AuthRole exists = authRoleRepository.findOne(query);
        AssertUtil.throwErrWhenTrue(exists != null && !Objects.equals(exists.getRoleId(), excludeRoleId),
                ErrorCodeEnum.ROLE_KEY_EXISTS, "角色标识已存在");
    }

    /** 校验事务结果，失败抛业务异常。 */
    private void checkResult(Result<?> result) {
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
    }
}
