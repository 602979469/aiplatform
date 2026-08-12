package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysRoleCreateRequest;
import com.jakt.aiplatform.web.param.SysRoleQueryRequest;
import com.jakt.aiplatform.web.param.SysRoleUpdateRequest;
import com.jakt.aiplatform.web.result.SysRoleResponse;
import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.param.SysRoleQueryParam;

/**
 * 角色对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysRoleAssembler {

    private SysRoleAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建角色请求 DTO
     * @return 角色领域模型
     */
    public static SysRole toModel(SysRoleCreateRequest request) {
        SysRole sysRole = new SysRole();
        sysRole.setRoleName(request.getRoleName());
        sysRole.setRoleKey(request.getRoleKey());
        sysRole.setRoleSort(request.getRoleSort());
        sysRole.setDataScope(request.getDataScope());
        sysRole.setStatus(request.getStatus());
        sysRole.setRemark(request.getRemark());
        return sysRole;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新角色请求 DTO
     * @param id      路径中的角色 ID
     * @return 角色领域模型
     */
    public static SysRole toModel(SysRoleUpdateRequest request, Long id) {
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(id);
        sysRole.setRoleName(request.getRoleName());
        sysRole.setRoleKey(request.getRoleKey());
        sysRole.setRoleSort(request.getRoleSort());
        sysRole.setDataScope(request.getDataScope());
        sysRole.setStatus(request.getStatus());
        sysRole.setRemark(request.getRemark());
        return sysRole;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 角色查询请求 DTO
     * @return 角色查询参数
     */
    public static SysRoleQueryParam toQueryParam(SysRoleQueryRequest request) {
        SysRoleQueryParam param = new SysRoleQueryParam();
        param.setRoleId(request.getRoleId());
        param.setRoleName(request.getRoleName());
        param.setRoleKey(request.getRoleKey());
        param.setRoleSort(request.getRoleSort());
        param.setDataScope(request.getDataScope());
        param.setStatus(request.getStatus());
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
     * @param sysRole 角色领域模型
     * @return 角色响应 VO
     */
    public static SysRoleResponse toResponse(SysRole sysRole) {
        SysRoleResponse response = new SysRoleResponse();
        response.setRoleId(sysRole.getRoleId());
        response.setRoleName(sysRole.getRoleName());
        response.setRoleKey(sysRole.getRoleKey());
        response.setRoleSort(sysRole.getRoleSort());
        response.setDataScope(sysRole.getDataScope());
        response.setStatus(sysRole.getStatus());
        response.setRemark(sysRole.getRemark());
        response.setCreateTime(sysRole.getCreateTime());
        response.setUpdateTime(sysRole.getUpdateTime());
        return response;
    }
}
