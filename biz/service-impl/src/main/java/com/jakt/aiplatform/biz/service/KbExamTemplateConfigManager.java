package com.jakt.aiplatform.biz.service;

import java.util.List;

/**
 * 试卷模板（配置管理）用例编排：列表、详情、保存（含知识点规则）、删除。
 *
 * <p>注意：与生成器产出的 {@link KbExamTemplateManager}（模板表 CRUD）区分，本接口面向"配置管理"页面。
 */
public interface KbExamTemplateConfigManager {

    /**
     * 模板列表：全局已发布 + 自己的个人模板。
     *
     * @param userId 当前用户ID
     * @return 模板列表
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
     * @param view   模板内容
     * @return 模板ID
     */
    Long save(Long userId, KbExamTemplateView view);

    /**
     * 删除模板（仅本人模板，或管理员删全局模板）。
     *
     * @param userId  当前用户ID
     * @param id      模板ID
     * @param isAdmin 是否管理员
     */
    void delete(Long userId, Long id, boolean isAdmin);
}
