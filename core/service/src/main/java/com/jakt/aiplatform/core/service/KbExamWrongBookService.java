package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.dto.KbExamWrongView;

/**
 * 错题集领域服务：错题列表与「标记已掌握」。
 */
public interface KbExamWrongBookService {

    /**
     * 错题集分页列表。
     *
     * @param userId 用户ID
     * @param category 分类（可空）
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<KbExamWrongView> wrongBook(Long userId, String category, Integer pageNum, Integer pageSize);

    /**
     * 标记题目已掌握（移出错题集）。
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     */
    void markMastered(Long userId, Long questionId);
}
