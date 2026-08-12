package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysUserCreateRequest;
import com.jakt.aiplatform.web.param.SysUserQueryRequest;
import com.jakt.aiplatform.web.param.SysUserUpdateRequest;
import com.jakt.aiplatform.web.result.SysUserResponse;
import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;

/**
 * 用户对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysUserAssembler {

    private SysUserAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建用户请求 DTO
     * @return 用户领域模型
     */
    public static SysUser toModel(SysUserCreateRequest request) {
        SysUser sysUser = new SysUser();
        sysUser.setDeptId(request.getDeptId());
        sysUser.setLoginName(request.getLoginName());
        sysUser.setUserName(request.getUserName());
        sysUser.setUserType(request.getUserType());
        sysUser.setEmail(request.getEmail());
        sysUser.setPhonenumber(request.getPhonenumber());
        sysUser.setSex(request.getSex());
        sysUser.setAvatar(request.getAvatar());
        sysUser.setPassword(request.getPassword());
        sysUser.setSalt(request.getSalt());
        sysUser.setStatus(request.getStatus());
        sysUser.setLoginIp(request.getLoginIp());
        sysUser.setLoginDate(request.getLoginDate());
        sysUser.setPwdUpdateDate(request.getPwdUpdateDate());
        sysUser.setRemark(request.getRemark());
        return sysUser;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新用户请求 DTO
     * @param id      路径中的用户 ID
     * @return 用户领域模型
     */
    public static SysUser toModel(SysUserUpdateRequest request, Long id) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(id);
        sysUser.setDeptId(request.getDeptId());
        sysUser.setLoginName(request.getLoginName());
        sysUser.setUserName(request.getUserName());
        sysUser.setUserType(request.getUserType());
        sysUser.setEmail(request.getEmail());
        sysUser.setPhonenumber(request.getPhonenumber());
        sysUser.setSex(request.getSex());
        sysUser.setAvatar(request.getAvatar());
        sysUser.setPassword(request.getPassword());
        sysUser.setSalt(request.getSalt());
        sysUser.setStatus(request.getStatus());
        sysUser.setLoginIp(request.getLoginIp());
        sysUser.setLoginDate(request.getLoginDate());
        sysUser.setPwdUpdateDate(request.getPwdUpdateDate());
        sysUser.setRemark(request.getRemark());
        return sysUser;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 用户查询请求 DTO
     * @return 用户查询参数
     */
    public static SysUserQueryParam toQueryParam(SysUserQueryRequest request) {
        SysUserQueryParam param = new SysUserQueryParam();
        param.setUserId(request.getUserId());
        param.setDeptId(request.getDeptId());
        param.setLoginName(request.getLoginName());
        param.setUserName(request.getUserName());
        param.setUserType(request.getUserType());
        param.setEmail(request.getEmail());
        param.setPhonenumber(request.getPhonenumber());
        param.setSex(request.getSex());
        param.setAvatar(request.getAvatar());
        param.setPassword(request.getPassword());
        param.setSalt(request.getSalt());
        param.setStatus(request.getStatus());
        param.setLoginIp(request.getLoginIp());
        param.setLoginDate(request.getLoginDate());
        param.setPwdUpdateDate(request.getPwdUpdateDate());
        param.setRemark(request.getRemark());
        param.setCreateTimeBegin(request.getCreateTimeBegin());
        param.setCreateTimeEnd(request.getCreateTimeEnd());
        param.setUpdateTimeBegin(request.getUpdateTimeBegin());
        param.setUpdateTimeEnd(request.getUpdateTimeEnd());
        param.setPageNum(ObjectUtil.defaultIfNull(request.getPageNum(), 1));
        param.setPageSize(ObjectUtil.defaultIfNull(request.getPageSize(), 10));
        return param;
    }

    /**
     * 领域模型 → 响应 VO。
     *
     * @param sysUser 用户领域模型
     * @return 用户响应 VO
     */
    public static SysUserResponse toResponse(SysUser sysUser) {
        SysUserResponse response = new SysUserResponse();
        response.setUserId(sysUser.getUserId());
        response.setDeptId(sysUser.getDeptId());
        response.setLoginName(sysUser.getLoginName());
        response.setUserName(sysUser.getUserName());
        response.setUserType(sysUser.getUserType());
        response.setEmail(sysUser.getEmail());
        response.setPhonenumber(sysUser.getPhonenumber());
        response.setSex(sysUser.getSex());
        response.setAvatar(sysUser.getAvatar());
        response.setPassword(sysUser.getPassword());
        response.setSalt(sysUser.getSalt());
        response.setStatus(sysUser.getStatus());
        response.setLoginIp(sysUser.getLoginIp());
        response.setLoginDate(sysUser.getLoginDate());
        response.setPwdUpdateDate(sysUser.getPwdUpdateDate());
        response.setRemark(sysUser.getRemark());
        response.setCreateTime(sysUser.getCreateTime());
        response.setUpdateTime(sysUser.getUpdateTime());
        return response;
    }
}
