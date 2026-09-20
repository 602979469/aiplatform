package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.dto.KbExamTemplateView;

import java.util.List;

/**
 * 试卷模板配置领域服务：列表、详情、保存（含知识点规则整体替换）、删除。
 */
public interface KbExamTemplateConfigService {

    /**
     * 模板列表：全局已发布 + 自己的个人模板。
     *
     * @param userId 当前用户ID
     * @return 模板视图列表
     */
    List<KbExamTemplateView> list(Long userId);

    /**
     * 模板详情（含规则）。
     *
     * @param id 模板ID
     * @return 模板视图
     */
    KbExamTemplateView get(Long id);

    /**
     * 保存模板（新增或修改，规则整体替换）。
     *
     * @param userId 当前用户ID
     * @param view 模板内容
     * @return 模板ID
     */
    Long save(Long userId, KbExamTemplateView view);

    /**
     * 删除模板（仅本人模板，或管理员删全局模板）。
     *
     * @param userId 当前用户ID
     * @param id 模板ID
     * @param isAdmin 是否管理员
     */
    void delete(Long userId, Long id, boolean isAdmin);
}
