package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.dal.dataobject.KbQuestionDO;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;

/**
 * 题库管理用例编排：分页查询、详情、新增、修改、物理删除、知识点元数据。
 */
public interface KbQuestionAdminManager {

    /**
     * 分页查询题目（关键词 + 知识点/题型/难度筛选）。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbQuestionDO> page(KbQuestionQueryParam query);

    /**
     * 查询题目详情。
     *
     * @param id 题目ID
     * @return 题目（不存在时抛业务异常）
     */
    KbQuestionDO get(Long id);

    /**
     * 新增题目。
     *
     * @param question 题目
     * @return 新增后的主键
     */
    Long create(KbQuestionDO question);

    /**
     * 修改题目（全量字段）。
     *
     * @param question 题目（含 id）
     */
    void update(KbQuestionDO question);

    /**
     * 物理删除题目。
     *
     * @param id 题目ID
     */
    void delete(Long id);

    /**
     * 分类 / 子主题汇总（知识点题量），供筛选下拉使用。
     *
     * @return 元数据
     */
    KbQuestionMetaView meta();
}
