package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysUserOnlineCreateRequest;
import com.jakt.aiplatform.web.param.SysUserOnlineQueryRequest;
import com.jakt.aiplatform.web.param.SysUserOnlineUpdateRequest;
import com.jakt.aiplatform.web.result.SysUserOnlineResponse;
import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;

/**
 * 在线用户对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysUserOnlineAssembler {

    private SysUserOnlineAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建在线用户请求 DTO
     * @return 在线用户领域模型
     */
    public static SysUserOnline toModel(SysUserOnlineCreateRequest request) {
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setSessionId(request.getSessionId());
        sysUserOnline.setLoginName(request.getLoginName());
        sysUserOnline.setDeptName(request.getDeptName());
        sysUserOnline.setIpaddr(request.getIpaddr());
        sysUserOnline.setLoginLocation(request.getLoginLocation());
        sysUserOnline.setBrowser(request.getBrowser());
        sysUserOnline.setOs(request.getOs());
        sysUserOnline.setStatus(request.getStatus());
        sysUserOnline.setStartTimestamp(request.getStartTimestamp());
        sysUserOnline.setLastAccessTime(request.getLastAccessTime());
        sysUserOnline.setExpireTime(request.getExpireTime());
        sysUserOnline.setSessionData(request.getSessionData());
        return sysUserOnline;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新在线用户请求 DTO
     * @param id      路径中的在线用户 ID
     * @return 在线用户领域模型
     */
    public static SysUserOnline toModel(SysUserOnlineUpdateRequest request, String id) {
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setSessionId(id);
        sysUserOnline.setLoginName(request.getLoginName());
        sysUserOnline.setDeptName(request.getDeptName());
        sysUserOnline.setIpaddr(request.getIpaddr());
        sysUserOnline.setLoginLocation(request.getLoginLocation());
        sysUserOnline.setBrowser(request.getBrowser());
        sysUserOnline.setOs(request.getOs());
        sysUserOnline.setStatus(request.getStatus());
        sysUserOnline.setStartTimestamp(request.getStartTimestamp());
        sysUserOnline.setLastAccessTime(request.getLastAccessTime());
        sysUserOnline.setExpireTime(request.getExpireTime());
        sysUserOnline.setSessionData(request.getSessionData());
        return sysUserOnline;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 在线用户查询请求 DTO
     * @return 在线用户查询参数
     */
    public static SysUserOnlineQueryParam toQueryParam(SysUserOnlineQueryRequest request) {
        SysUserOnlineQueryParam param = new SysUserOnlineQueryParam();
        param.setSessionId(request.getSessionId());
        param.setLoginName(request.getLoginName());
        param.setDeptName(request.getDeptName());
        param.setIpaddr(request.getIpaddr());
        param.setLoginLocation(request.getLoginLocation());
        param.setBrowser(request.getBrowser());
        param.setOs(request.getOs());
        param.setStatus(request.getStatus());
        param.setStartTimestamp(request.getStartTimestamp());
        param.setLastAccessTime(request.getLastAccessTime());
        param.setExpireTime(request.getExpireTime());
        param.setSessionData(request.getSessionData());
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
     * @param sysUserOnline 在线用户领域模型
     * @return 在线用户响应 VO
     */
    public static SysUserOnlineResponse toResponse(SysUserOnline sysUserOnline) {
        SysUserOnlineResponse response = new SysUserOnlineResponse();
        response.setSessionId(sysUserOnline.getSessionId());
        response.setLoginName(sysUserOnline.getLoginName());
        response.setDeptName(sysUserOnline.getDeptName());
        response.setIpaddr(sysUserOnline.getIpaddr());
        response.setLoginLocation(sysUserOnline.getLoginLocation());
        response.setBrowser(sysUserOnline.getBrowser());
        response.setOs(sysUserOnline.getOs());
        response.setStatus(sysUserOnline.getStatus());
        response.setStartTimestamp(sysUserOnline.getStartTimestamp());
        response.setLastAccessTime(sysUserOnline.getLastAccessTime());
        response.setExpireTime(sysUserOnline.getExpireTime());
        response.setSessionData(sysUserOnline.getSessionData());
        response.setCreateTime(sysUserOnline.getCreateTime());
        response.setUpdateTime(sysUserOnline.getUpdateTime());
        return response;
    }
}
