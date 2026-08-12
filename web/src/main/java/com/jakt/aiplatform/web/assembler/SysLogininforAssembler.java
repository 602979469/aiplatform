package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysLogininforCreateRequest;
import com.jakt.aiplatform.web.param.SysLogininforQueryRequest;
import com.jakt.aiplatform.web.param.SysLogininforUpdateRequest;
import com.jakt.aiplatform.web.result.SysLogininforResponse;
import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.model.param.SysLogininforQueryParam;

/**
 * 登录日志对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysLogininforAssembler {

    private SysLogininforAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建登录日志请求 DTO
     * @return 登录日志领域模型
     */
    public static SysLogininfor toModel(SysLogininforCreateRequest request) {
        SysLogininfor sysLogininfor = new SysLogininfor();
        sysLogininfor.setLoginName(request.getLoginName());
        sysLogininfor.setIpaddr(request.getIpaddr());
        sysLogininfor.setLoginLocation(request.getLoginLocation());
        sysLogininfor.setBrowser(request.getBrowser());
        sysLogininfor.setOs(request.getOs());
        sysLogininfor.setStatus(request.getStatus());
        sysLogininfor.setMsg(request.getMsg());
        sysLogininfor.setLoginTime(request.getLoginTime());
        return sysLogininfor;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新登录日志请求 DTO
     * @param id      路径中的登录日志 ID
     * @return 登录日志领域模型
     */
    public static SysLogininfor toModel(SysLogininforUpdateRequest request, Long id) {
        SysLogininfor sysLogininfor = new SysLogininfor();
        sysLogininfor.setInfoId(id);
        sysLogininfor.setLoginName(request.getLoginName());
        sysLogininfor.setIpaddr(request.getIpaddr());
        sysLogininfor.setLoginLocation(request.getLoginLocation());
        sysLogininfor.setBrowser(request.getBrowser());
        sysLogininfor.setOs(request.getOs());
        sysLogininfor.setStatus(request.getStatus());
        sysLogininfor.setMsg(request.getMsg());
        sysLogininfor.setLoginTime(request.getLoginTime());
        return sysLogininfor;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 登录日志查询请求 DTO
     * @return 登录日志查询参数
     */
    public static SysLogininforQueryParam toQueryParam(SysLogininforQueryRequest request) {
        SysLogininforQueryParam param = new SysLogininforQueryParam();
        param.setInfoId(request.getInfoId());
        param.setLoginName(request.getLoginName());
        param.setIpaddr(request.getIpaddr());
        param.setLoginLocation(request.getLoginLocation());
        param.setBrowser(request.getBrowser());
        param.setOs(request.getOs());
        param.setStatus(request.getStatus());
        param.setMsg(request.getMsg());
        param.setLoginTime(request.getLoginTime());
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
     * @param sysLogininfor 登录日志领域模型
     * @return 登录日志响应 VO
     */
    public static SysLogininforResponse toResponse(SysLogininfor sysLogininfor) {
        SysLogininforResponse response = new SysLogininforResponse();
        response.setInfoId(sysLogininfor.getInfoId());
        response.setLoginName(sysLogininfor.getLoginName());
        response.setIpaddr(sysLogininfor.getIpaddr());
        response.setLoginLocation(sysLogininfor.getLoginLocation());
        response.setBrowser(sysLogininfor.getBrowser());
        response.setOs(sysLogininfor.getOs());
        response.setStatus(sysLogininfor.getStatus());
        response.setMsg(sysLogininfor.getMsg());
        response.setLoginTime(sysLogininfor.getLoginTime());
        response.setCreateTime(sysLogininfor.getCreateTime());
        response.setUpdateTime(sysLogininfor.getUpdateTime());
        return response;
    }
}
