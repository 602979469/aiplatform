package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.param.KbExamTemplateQueryParam;

import java.util.List;

/**
 * 试卷模板领域服务
 *
 * 实现类为 KbExamTemplateServiceImpl（core.service.impl 包）。
 */
public interface KbExamTemplateService {

    /**
     * 创建试卷模板
     *
     * @param kbExamTemplate 试卷模板
     * @return 创建后的试卷模板（主键已回填）
     */
    KbExamTemplate createKbExamTemplate(KbExamTemplate kbExamTemplate);

    /**
     * 更新试卷模板（全量）
     *
     * @param kbExamTemplate 试卷模板（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateKbExamTemplate(KbExamTemplate kbExamTemplate);

    /**
     * 按条件更新试卷模板（只更新传入的非空字段）。
     *
     * @param kbExamTemplate 试卷模板（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(KbExamTemplate kbExamTemplate);

    /**
     * 删除试卷模板
     *
     * @param id 试卷模板主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteKbExamTemplate(Long id);

    /**
     * 按主键获取试卷模板
     *
     * @param id 试卷模板主键
     * @return 试卷模板
     */
    KbExamTemplate getKbExamTemplate(Long id);

    /**
     * 分页查询试卷模板
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbExamTemplate> findPage(KbExamTemplateQueryParam query);

    /**
     * 列表查询试卷模板
     *
     * @param query 查询参数
     * @return 试卷模板列表
     */
    List<KbExamTemplate> findList(KbExamTemplateQueryParam query);
}
