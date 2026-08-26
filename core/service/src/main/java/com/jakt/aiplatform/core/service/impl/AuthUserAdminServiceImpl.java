package com.jakt.aiplatform.core.service.impl;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.result.Result;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import com.jakt.aiplatform.core.repository.AuthUserRepository;
import com.jakt.aiplatform.core.service.AuthUserAdminService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;

/**
 * 用户管理领域服务实现：跨表多写统一走 BizTemplate。
 */
@Service
public class AuthUserAdminServiceImpl implements AuthUserAdminService {

    /** 允许的头像图片扩展名。 */
    private static final Set<String> AVATAR_EXTS = Set.of("png", "jpg", "jpeg", "gif", "webp");

    private final AuthUserRepository authUserRepository;

    private final TransactionTemplate transactionTemplate;

    /** 头像存储目录。 */
    private final String avatarDir;

    public AuthUserAdminServiceImpl(AuthUserRepository authUserRepository,
                                    TransactionTemplate transactionTemplate,
                                    @Value("${aiplatform.upload.avatar-dir:./uploads/avatar}") String avatarDir) {
        this.authUserRepository = authUserRepository;
        this.transactionTemplate = transactionTemplate;
        this.avatarDir = avatarDir;
    }

    @Override
    public PageResult<AuthUser> pageUser(AuthUserQueryParam query) {
        return authUserRepository.findPage(query);
    }

    @Override
    public AuthUser getUser(Long userId) {
        AuthUser user = authUserRepository.findById(userId);
        AssertUtil.throwErrWhenNull(user, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
        return user;
    }

    @Override
    public AuthUser createUser(AuthUser user, List<Long> roleIds) {
        AssertUtil.throwErrWhenTrue(authUserRepository.findByUsername(user.getUsername()) != null,
                BizErrorCodeEnum.USERNAME_EXISTS);
        user.setNickname(StrUtil.blankToDefault(user.getNickname(), user.getUsername()));
        user.setEmail(StrUtil.nullToEmpty(user.getEmail()));
        user.setAvatar(StrUtil.nullToEmpty(user.getAvatar()));
        if (user.getStatus() == null) {
            user.setStatus(EnableStatusEnum.ENABLE);
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        Result<AuthUser> result = BizTemplate.execute(transactionTemplate, () -> {
            authUserRepository.insert(user);
            if (CollUtil.isNotEmpty(roleIds)) {
                authUserRepository.replaceRoles(user.getUserId(), roleIds);
            }
            return user;
        });
        checkResult(result);
        return result.getData();
    }

    @Override
    public void updateUser(AuthUser user) {
        int affected = authUserRepository.updateByCondition(user);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void changeUserStatus(Long userId, EnableStatusEnum status) {
        AuthUser update = new AuthUser();
        update.setUserId(userId);
        update.setStatus(status);
        int affected = authUserRepository.updateByCondition(update);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
        if (status == EnableStatusEnum.DISABLE) {
            StpUtil.logout(userId);
        }
    }

    @Override
    public void resetPassword(Long userId, String password) {
        AuthUser update = new AuthUser();
        update.setUserId(userId);
        update.setPassword(BCrypt.hashpw(password));
        int affected = authUserRepository.updateByCondition(update);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        AuthUser user = authUserRepository.findById(userId);
        AssertUtil.throwErrWhenNull(user, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
        AssertUtil.throwErrWhenFalse(BCrypt.checkpw(oldPassword, user.getPassword()),
                BizErrorCodeEnum.OLD_PASSWORD_ERROR);
        AuthUser update = new AuthUser();
        update.setUserId(userId);
        update.setPassword(BCrypt.hashpw(newPassword));
        int affected = authUserRepository.updateByCondition(update);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public String updateAvatar(Long userId, byte[] imageBytes, String originalFilename) {
        AuthUser user = authUserRepository.findById(userId);
        AssertUtil.throwErrWhenNull(user, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
        String ext = FileNameUtil.extName(originalFilename);
        AssertUtil.throwErrWhenFalse(StrUtil.isNotBlank(ext) && AVATAR_EXTS.contains(ext.toLowerCase()),
                ErrorCodeEnum.PARAM_INVALID, "头像仅支持 png/jpg/jpeg/gif/webp");
        String fileName = userId + "_" + System.currentTimeMillis() + "." + ext.toLowerCase();
        FileUtil.writeBytes(imageBytes, avatarDir + "/" + fileName);
        String avatarUrl = "/uploads/avatar/" + fileName;
        AuthUser update = new AuthUser();
        update.setUserId(userId);
        update.setAvatar(avatarUrl);
        int affected = authUserRepository.updateByCondition(update);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
        return avatarUrl;
    }

    @Override
    public void updateProfile(Long userId, String nickname, String email) {
        AuthUser update = new AuthUser();
        update.setUserId(userId);
        update.setNickname(nickname);
        update.setEmail(email);
        int affected = authUserRepository.updateByCondition(update);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        getUser(userId);
        checkResult(BizTemplate.executeWithoutResult(transactionTemplate,
                () -> authUserRepository.replaceRoles(userId, roleIds)));
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        getUser(userId);
        return authUserRepository.findRoleIdsByUserId(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        getUser(userId);
        checkResult(BizTemplate.executeWithoutResult(transactionTemplate,
                () -> {
                    authUserRepository.clearUserRoles(userId);
                    authUserRepository.deleteById(userId);
                }));
        StpUtil.logout(userId);
    }

    /** 校验事务结果，失败抛业务异常。 */
    private void checkResult(Result<?> result) {
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
    }
}
