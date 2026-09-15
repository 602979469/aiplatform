package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.param.KbExamTemplateRuleQueryParam;

import java.util.List;

/**
 * 试卷模板知识点规则管理类接口定义
 */
public interface KbExamTemplateRuleManager {

    /**
     * 创建试卷模板知识点规则
     *
     * @param kbExamTemplateRule 试卷模板知识点规则
     * @return 创建成功后的试卷模板知识点规则
     */
    KbExamTemplateRule createKbExamTemplateRule(KbExamTemplateRule kbExamTemplateRule);

    /**
     * 按主键查询试卷模板知识点规则
     *
     * @param id 试卷模板知识点规则主键
     * @return 试卷模板知识点规则
     */
    KbExamTemplateRule getKbExamTemplateRule(Long id);

    /**
     * 分页查询试卷模板知识点规则
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbExamTemplateRule> pageKbExamTemplateRules(KbExamTemplateRuleQueryParam query);

    /**
     * 列表查询试卷模板知识点规则
     *
     * @param query 查询参数
     * @return 试卷模板知识点规则列表
     */
    List<KbExamTemplateRule> listKbExamTemplateRules(KbExamTemplateRuleQueryParam query);

    /**
     * 更新试卷模板知识点规则（全量）。
     *
     * @param kbExamTemplateRule 试卷模板知识点规则（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateKbExamTemplateRule(KbExamTemplateRule kbExamTemplateRule);

    /**
     * 按条件更新试卷模板知识点规则（只更新传入的非空字段）。
     *
     * @param kbExamTemplateRule 试卷模板知识点规则（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(KbExamTemplateRule kbExamTemplateRule);

    /**
     * 删除试卷模板知识点规则。
     *
     * @param id 试卷模板知识点规则主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteKbExamTemplateRule(Long id);
}
