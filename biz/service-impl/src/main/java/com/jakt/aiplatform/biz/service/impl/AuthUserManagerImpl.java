package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AuthUserManager;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.constant.FileConstants;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.FileNamespaceEnum;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.core.service.AuthUserAdminService;
import com.jakt.aiplatform.core.service.FileInfoService;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.util.List;

    /** 用户管理用例编排实现：业务委托用户管理领域服务。 */
@Service
public class AuthUserManagerImpl implements AuthUserManager {

    private final AuthUserAdminService authUserAdminService;

    /** 文件领域服务（头像文件存储）。 */
    private final FileInfoService fileInfoService;

    public AuthUserManagerImpl(AuthUserAdminService authUserAdminService, FileInfoService fileInfoService) {
        this.authUserAdminService = authUserAdminService;
        this.fileInfoService = fileInfoService;
    }

    @Override
    public PageResult<AuthUser> pageUser(AuthUserQueryParam query) {
        return authUserAdminService.pageUser(query);
    }

    @Override
    public AuthUser getUser(Long userId) {
        return authUserAdminService.getUser(userId);
    }

    @Override
    public AuthUser createUser(AuthUser user, List<Long> roleIds) {
        return authUserAdminService.createUser(user, roleIds);
    }

    @Override
    public void updateUser(AuthUser user) {
        authUserAdminService.updateUser(user);
    }

    @Override
    public void changeUserStatus(Long userId, EnableStatusEnum status) {
        authUserAdminService.changeUserStatus(userId, status);
    }

    @Override
    public void resetPassword(Long userId, String password) {
        authUserAdminService.resetPassword(userId, password);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        authUserAdminService.updatePassword(userId, oldPassword, newPassword);
    }

    @Override
    public String updateAvatar(Long userId, byte[] imageBytes, String originalFilename) {
        AuthUser user = authUserAdminService.getUser(userId);
        FileInfo fileInfo = fileInfoService.upload(FileNamespaceEnum.USER_AVATAR.getCode(),
                imageBytes, originalFilename, null);
        String avatarUrl = FileConstants.AVATAR_URL_PREFIX + fileInfo.getId();
        authUserAdminService.updateAvatar(userId, avatarUrl);
        deleteOldAvatar(user.getAvatar());
        return avatarUrl;
    }

    /**
     * 删除旧头像文件（仅处理文件管理模块产生的头像，失败不影响主流程）。
     *
     * @param oldAvatar 旧头像访问路径
     */
    private void deleteOldAvatar(String oldAvatar) {
        Long oldFileId = parseAvatarFileId(oldAvatar);
        if (ObjectUtil.isNull(oldFileId)) {
            return;
        }
        try {
            fileInfoService.delete(oldFileId, FileNamespaceEnum.USER_AVATAR.getCode());
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "旧头像删除失败，忽略 id={}", oldFileId);
        }
    }

    /**
     * 从头像访问路径解析文件主键；非文件管理头像返回 null。
     *
     * @param avatarUrl 头像访问路径
     * @return 文件主键
     */
    private Long parseAvatarFileId(String avatarUrl) {
        if (StrUtil.isBlank(avatarUrl) || !StrUtil.startWith(avatarUrl, FileConstants.AVATAR_URL_PREFIX)) {
            return null;
        }
        return Convert.toLong(StrUtil.removePrefix(avatarUrl, FileConstants.AVATAR_URL_PREFIX), null);
    }

    @Override
    public void updateProfile(Long userId, String nickname, String email) {
        authUserAdminService.updateProfile(userId, nickname, email);
    }

    @Override
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        authUserAdminService.assignUserRoles(userId, roleIds);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return authUserAdminService.getUserRoleIds(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        authUserAdminService.deleteUser(userId);
    }
}
