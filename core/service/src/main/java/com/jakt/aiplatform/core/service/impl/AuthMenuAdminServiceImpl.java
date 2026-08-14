package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import com.jakt.aiplatform.core.model.exception.AiPlatformException;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;
import com.jakt.aiplatform.common.util.result.Result;
import com.jakt.aiplatform.common.util.template.BizTemplate;
import com.jakt.aiplatform.common.util.template.TransactionTemplate;
import com.jakt.aiplatform.core.repository.AuthMenuRepository;
import com.jakt.aiplatform.core.service.AuthMenuAdminService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理领域服务实现：树组装与跨表多写统一走事务模板。
 */
@Service
public class AuthMenuAdminServiceImpl implements AuthMenuAdminService {

    private final AuthMenuRepository authMenuRepository;

    private final TransactionTemplate transactionTemplate;

    public AuthMenuAdminServiceImpl(AuthMenuRepository authMenuRepository,
                                    TransactionTemplate transactionTemplate) {
        this.authMenuRepository = authMenuRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public AuthMenu getMenu(Long menuId) {
        AuthMenu menu = authMenuRepository.findById(menuId);
        AssertUtil.throwErrWhenNull(menu, ErrorCodeEnum.RESOURCE_NOT_FOUND, "菜单不存在");
        return menu;
    }

    @Override
    public List<AuthMenu> menuTree() {
        return buildTree(authMenuRepository.findList(new AuthMenuQueryParam()));
    }

    @Override
    public List<AuthMenu> menuList(AuthMenuQueryParam query) {
        return authMenuRepository.findList(query);
    }

    @Override
    public AuthMenu createMenu(AuthMenu menu) {
        menu.setParentId(menu.getParentId() == null ? 0L : menu.getParentId());
        menu.setOrderNum(menu.getOrderNum() == null ? 0 : menu.getOrderNum());
        menu.setPath(StrUtil.nullToEmpty(menu.getPath()));
        menu.setComponent(StrUtil.nullToEmpty(menu.getComponent()));
        if (menu.getVisible() == null) {
            menu.setVisible(VisibleEnum.SHOW);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(EnableStatusEnum.ENABLE);
        }
        menu.setIcon(StrUtil.nullToEmpty(menu.getIcon()));
        authMenuRepository.insert(menu);
        return menu;
    }

    @Override
    public void updateMenu(AuthMenu menu) {
        int affected = authMenuRepository.updateByCondition(menu);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteMenu(Long menuId) {
        AuthMenuQueryParam query = new AuthMenuQueryParam();
        query.setParentId(menuId);
        AssertUtil.throwErrWhenTrue(CollUtil.isNotEmpty(authMenuRepository.findList(query)),
                ErrorCodeEnum.MENU_HAS_CHILDREN, "存在子菜单，禁止删除");
        checkResult(BizTemplate.executeWithoutResult(transactionTemplate,
                () -> {
                    authMenuRepository.clearMenuBindings(menuId);
                    authMenuRepository.deleteById(menuId);
                }));
    }

    /** 校验事务结果，失败抛业务异常。 */
    private void checkResult(Result<?> result) {
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
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
