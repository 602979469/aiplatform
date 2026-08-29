package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;
import com.jakt.aiplatform.web.param.FileInfoQueryRequest;
import com.jakt.aiplatform.web.result.FileInfoResponse;

/**
 * 文件对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class FileInfoAssembler {

    private FileInfoAssembler() {
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 查询参数
     */
    public static FileInfoQueryParam toQueryParam(FileInfoQueryRequest request) {
        if (request == null) {
            return new FileInfoQueryParam();
        }
        FileInfoQueryParam param = new FileInfoQueryParam();
        param.setNamespace(request.getNamespace());
        param.setOriginalName(request.getFileName());
        param.setPageNum(ObjectUtil.defaultIfNull(request.getPageNum(), PageConstants.DEFAULT_PAGE_NUM));
        param.setPageSize(ObjectUtil.defaultIfNull(request.getPageSize(), PageConstants.DEFAULT_PAGE_SIZE));
        return param;
    }

    /**
     * 领域模型 → 响应 VO。
     *
     * @param fileInfo 文件信息领域模型；为空返回 null
     * @return 文件信息响应 VO
     */
    public static FileInfoResponse toResponse(FileInfo fileInfo) {
        if (fileInfo == null) {
            return null;
        }
        FileInfoResponse response = new FileInfoResponse();
        response.setId(fileInfo.getId());
        response.setNamespace(fileInfo.getNamespace());
        response.setOriginalName(fileInfo.getOriginalName());
        response.setFileSize(fileInfo.getFileSize());
        response.setFileType(fileInfo.getFileType());
        response.setRemark(fileInfo.getRemark());
        response.setCreateBy(fileInfo.getCreateBy());
        response.setCreateTime(fileInfo.getCreateTime());
        response.setUpdateTime(fileInfo.getUpdateTime());
        return response;
    }
}
