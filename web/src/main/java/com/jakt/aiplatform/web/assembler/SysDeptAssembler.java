package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysDeptCreateRequest;
import com.jakt.aiplatform.web.param.SysDeptQueryRequest;
import com.jakt.aiplatform.web.param.SysDeptUpdateRequest;
import com.jakt.aiplatform.web.result.SysDeptResponse;
import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;

/**
 * 部门对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysDeptAssembler {

    private SysDeptAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建部门请求 DTO
     * @return 部门领域模型
     */
    public static SysDept toModel(SysDeptCreateRequest request) {
        SysDept sysDept = new SysDept();
        sysDept.setParentId(request.getParentId());
        sysDept.setAncestors(request.getAncestors());
        sysDept.setDeptName(request.getDeptName());
        sysDept.setOrderNum(request.getOrderNum());
        sysDept.setLeader(request.getLeader());
        sysDept.setPhone(request.getPhone());
        sysDept.setEmail(request.getEmail());
        sysDept.setStatus(request.getStatus());
        return sysDept;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新部门请求 DTO
     * @param id      路径中的部门 ID
     * @return 部门领域模型
     */
    public static SysDept toModel(SysDeptUpdateRequest request, Long id) {
        SysDept sysDept = new SysDept();
        sysDept.setDeptId(id);
        sysDept.setParentId(request.getParentId());
        sysDept.setAncestors(request.getAncestors());
        sysDept.setDeptName(request.getDeptName());
        sysDept.setOrderNum(request.getOrderNum());
        sysDept.setLeader(request.getLeader());
        sysDept.setPhone(request.getPhone());
        sysDept.setEmail(request.getEmail());
        sysDept.setStatus(request.getStatus());
        return sysDept;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 部门查询请求 DTO
     * @return 部门查询参数
     */
    public static SysDeptQueryParam toQueryParam(SysDeptQueryRequest request) {
        SysDeptQueryParam param = new SysDeptQueryParam();
        param.setDeptId(request.getDeptId());
        param.setParentId(request.getParentId());
        param.setAncestors(request.getAncestors());
        param.setDeptName(request.getDeptName());
        param.setOrderNum(request.getOrderNum());
        param.setLeader(request.getLeader());
        param.setPhone(request.getPhone());
        param.setEmail(request.getEmail());
        param.setStatus(request.getStatus());
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
     * @param sysDept 部门领域模型
     * @return 部门响应 VO
     */
    public static SysDeptResponse toResponse(SysDept sysDept) {
        SysDeptResponse response = new SysDeptResponse();
        response.setDeptId(sysDept.getDeptId());
        response.setParentId(sysDept.getParentId());
        response.setAncestors(sysDept.getAncestors());
        response.setDeptName(sysDept.getDeptName());
        response.setOrderNum(sysDept.getOrderNum());
        response.setLeader(sysDept.getLeader());
        response.setPhone(sysDept.getPhone());
        response.setEmail(sysDept.getEmail());
        response.setStatus(sysDept.getStatus());
        response.setCreateTime(sysDept.getCreateTime());
        response.setUpdateTime(sysDept.getUpdateTime());
        return response;
    }
}
