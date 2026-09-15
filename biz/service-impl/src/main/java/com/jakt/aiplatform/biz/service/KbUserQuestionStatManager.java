package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatQueryParam;

import java.util.List;

/**
 * 用户题目掌握状态管理类接口定义
 */
public interface KbUserQuestionStatManager {

    /**
     * 创建用户题目掌握状态
     *
     * @param kbUserQuestionStat 用户题目掌握状态
     * @return 创建成功后的用户题目掌握状态
     */
    KbUserQuestionStat createKbUserQuestionStat(KbUserQuestionStat kbUserQuestionStat);

    /**
     * 按主键查询用户题目掌握状态
     *
     * @param id 用户题目掌握状态主键
     * @return 用户题目掌握状态
     */
    KbUserQuestionStat getKbUserQuestionStat(Long id);

    /**
     * 分页查询用户题目掌握状态
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbUserQuestionStat> pageKbUserQuestionStats(KbUserQuestionStatQueryParam query);

    /**
     * 列表查询用户题目掌握状态
     *
     * @param query 查询参数
     * @return 用户题目掌握状态列表
     */
    List<KbUserQuestionStat> listKbUserQuestionStats(KbUserQuestionStatQueryParam query);

    /**
     * 更新用户题目掌握状态（全量）。
     *
     * @param kbUserQuestionStat 用户题目掌握状态（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateKbUserQuestionStat(KbUserQuestionStat kbUserQuestionStat);

    /**
     * 按条件更新用户题目掌握状态（只更新传入的非空字段）。
     *
     * @param kbUserQuestionStat 用户题目掌握状态（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(KbUserQuestionStat kbUserQuestionStat);

    /**
     * 删除用户题目掌握状态。
     *
     * @param id 用户题目掌握状态主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteKbUserQuestionStat(Long id);
}
