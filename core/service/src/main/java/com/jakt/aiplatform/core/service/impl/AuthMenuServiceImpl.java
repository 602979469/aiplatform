package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.repository.AuthMenuRepository;
import com.jakt.aiplatform.core.repository.AuthRoleRepository;
import com.jakt.aiplatform.core.service.AuthMenuService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单领域服务实现：菜单已按 parent_id/order_num 排序，按 parentId 分组组装树。
 */
@Service
public class AuthMenuServiceImpl implements AuthMenuService {

    private final AuthMenuRepository authMenuRepository;

    private final AuthRoleRepository authRoleRepository;

    public AuthMenuServiceImpl(AuthMenuRepository authMenuRepository, AuthRoleRepository authRoleRepository) {
        this.authMenuRepository = authMenuRepository;
        this.authRoleRepository = authRoleRepository;
    }

    @Override
    public List<AuthMenu> getRouters(Long userId) {
        // 超级管理员权限拉满：不走角色菜单授权，始终可见全部已配置菜单
        boolean superAdmin = AuthRole.containsSuperAdmin(authRoleRepository.findRoleKeysByUserId(userId));
        List<AuthMenu> menus = superAdmin
                ? authMenuRepository.findAllVisibleMenus()
                : authMenuRepository.findMenusByUserId(userId);
        return buildTree(menus);
    }

    /** 按 parentId 分组组装菜单树。 */
    private List<AuthMenu> buildTree(List<AuthMenu> menus) {
        Map<Long, List<AuthMenu>> byParent = menus.stream().collect(Collectors.groupingBy(
                AuthMenu::getParentId, LinkedHashMap::new, Collectors.toList()));
        return buildChildren(0L, byParent);
    }

    /** 递归挂接子菜单。 */
    private List<AuthMenu> buildChildren(Long parentId, Map<Long, List<AuthMenu>> byParent) {
        List<AuthMenu> children = byParent.getOrDefault(parentId, List.of());
        children.forEach(child -> child.setChildren(buildChildren(child.getMenuId(), byParent)));
        return children;
    }
}
