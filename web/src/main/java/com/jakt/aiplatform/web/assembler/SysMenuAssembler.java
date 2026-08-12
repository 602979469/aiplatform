package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysMenuCreateRequest;
import com.jakt.aiplatform.web.param.SysMenuQueryRequest;
import com.jakt.aiplatform.web.param.SysMenuUpdateRequest;
import com.jakt.aiplatform.web.result.SysMenuResponse;
import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;

/**
 * 菜单对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysMenuAssembler {

    private SysMenuAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建菜单请求 DTO
     * @return 菜单领域模型
     */
    public static SysMenu toModel(SysMenuCreateRequest request) {
        SysMenu sysMenu = new SysMenu();
        sysMenu.setMenuName(request.getMenuName());
        sysMenu.setParentId(request.getParentId());
        sysMenu.setOrderNum(request.getOrderNum());
        sysMenu.setUrl(request.getUrl());
        sysMenu.setTarget(request.getTarget());
        sysMenu.setMenuType(request.getMenuType());
        sysMenu.setVisible(request.getVisible());
        sysMenu.setIsRefresh(request.getIsRefresh());
        sysMenu.setPerms(request.getPerms());
        sysMenu.setIcon(request.getIcon());
        sysMenu.setRemark(request.getRemark());
        return sysMenu;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新菜单请求 DTO
     * @param id      路径中的菜单 ID
     * @return 菜单领域模型
     */
    public static SysMenu toModel(SysMenuUpdateRequest request, Long id) {
        SysMenu sysMenu = new SysMenu();
        sysMenu.setMenuId(id);
        sysMenu.setMenuName(request.getMenuName());
        sysMenu.setParentId(request.getParentId());
        sysMenu.setOrderNum(request.getOrderNum());
        sysMenu.setUrl(request.getUrl());
        sysMenu.setTarget(request.getTarget());
        sysMenu.setMenuType(request.getMenuType());
        sysMenu.setVisible(request.getVisible());
        sysMenu.setIsRefresh(request.getIsRefresh());
        sysMenu.setPerms(request.getPerms());
        sysMenu.setIcon(request.getIcon());
        sysMenu.setRemark(request.getRemark());
        return sysMenu;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 菜单查询请求 DTO
     * @return 菜单查询参数
     */
    public static SysMenuQueryParam toQueryParam(SysMenuQueryRequest request) {
        SysMenuQueryParam param = new SysMenuQueryParam();
        param.setMenuId(request.getMenuId());
        param.setMenuName(request.getMenuName());
        param.setParentId(request.getParentId());
        param.setOrderNum(request.getOrderNum());
        param.setUrl(request.getUrl());
        param.setTarget(request.getTarget());
        param.setMenuType(request.getMenuType());
        param.setVisible(request.getVisible());
        param.setIsRefresh(request.getIsRefresh());
        param.setPerms(request.getPerms());
        param.setIcon(request.getIcon());
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
     * @param sysMenu 菜单领域模型
     * @return 菜单响应 VO
     */
    public static SysMenuResponse toResponse(SysMenu sysMenu) {
        SysMenuResponse response = new SysMenuResponse();
        response.setMenuId(sysMenu.getMenuId());
        response.setMenuName(sysMenu.getMenuName());
        response.setParentId(sysMenu.getParentId());
        response.setOrderNum(sysMenu.getOrderNum());
        response.setUrl(sysMenu.getUrl());
        response.setTarget(sysMenu.getTarget());
        response.setMenuType(sysMenu.getMenuType());
        response.setVisible(sysMenu.getVisible());
        response.setIsRefresh(sysMenu.getIsRefresh());
        response.setPerms(sysMenu.getPerms());
        response.setIcon(sysMenu.getIcon());
        response.setRemark(sysMenu.getRemark());
        response.setCreateTime(sysMenu.getCreateTime());
        response.setUpdateTime(sysMenu.getUpdateTime());
        return response;
    }
}
