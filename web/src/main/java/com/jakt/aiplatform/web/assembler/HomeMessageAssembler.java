package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.HomeMessage;
import com.jakt.aiplatform.core.service.HomeMessageService;
import com.jakt.aiplatform.web.param.HomeMessageCreateRequest;
import com.jakt.aiplatform.web.result.HomeMessageResponse;

import java.time.ZoneId;
import java.util.List;

/**
 * 留言板组装器：DTO / 领域模型互转，只存在于 web。
 */
public final class HomeMessageAssembler {

    private HomeMessageAssembler() {
    }

    /**
     * 发布请求 → 领域模型。
     *
     * @param request 发布请求；为空返回 null
     * @return 留言领域模型
     */
    public static HomeMessage toModel(HomeMessageCreateRequest request) {
        if (request == null) {
            return null;
        }
        HomeMessage homeMessage = new HomeMessage();
        homeMessage.setContent(request.getContent());
        return homeMessage;
    }

    /**
     * 领域模型 → 响应 VO。
     *
     * @param homeMessage 留言领域模型；为空返回 null
     * @param clientIp    当前请求者 IP（只用于标记「我的留言」，不写进响应）
     * @return 留言响应 VO
     */
    public static HomeMessageResponse toResponse(HomeMessage homeMessage, String clientIp) {
        if (homeMessage == null) {
            return null;
        }
        HomeMessageResponse response = new HomeMessageResponse();
        response.setId(homeMessage.getId());
        response.setContent(homeMessage.getContent());
        response.setAvatarUrl(buildAvatarUrl(homeMessage.getAvatarFileId()));
        response.setAvatarName(homeMessage.getAvatarName());
        response.setColorIndex(homeMessage.getColorIndex());
        response.setMine(clientIp != null && clientIp.equals(homeMessage.getClientIp()));
        response.setCreateTimestamp(homeMessage.getCreateTime() == null ? null
                : homeMessage.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return response;
    }

    /**
     * 领域模型列表 → 响应 VO 列表。
     *
     * @param homeMessages 留言领域模型列表
     * @param clientIp     当前请求者 IP
     * @return 留言响应 VO 列表
     */
    public static List<HomeMessageResponse> toResponses(List<HomeMessage> homeMessages, String clientIp) {
        return ConvertUtil.map(homeMessages, homeMessage -> toResponse(homeMessage, clientIp));
    }

    /**
     * 头像直出地址：复用文件模块的公开预览接口。
     *
     * @param avatarFileId 头像文件ID；为空返回 null
     * @return 相对地址
     */
    private static String buildAvatarUrl(Long avatarFileId) {
        if (avatarFileId == null) {
            return null;
        }
        return "/api/file/" + avatarFileId + "/preview?namespace=" + HomeMessageService.AVATAR_NAMESPACE;
    }
}
