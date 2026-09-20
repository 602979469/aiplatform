package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.dto.KbExamWrongView;
import com.jakt.aiplatform.core.repository.KbUserQuestionStatRepository;
import com.jakt.aiplatform.core.service.KbExamWrongBookService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 错题集领域服务实现。
 */
@Service
public class KbExamWrongBookServiceImpl implements KbExamWrongBookService {

    /** 默认页码。 */
    private static final int DEFAULT_PAGE_NUM = 1;

    /** 默认每页条数。 */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /** 掌握度仓储。 */
    private final KbUserQuestionStatRepository kbUserQuestionStatRepository;

    public KbExamWrongBookServiceImpl(KbUserQuestionStatRepository kbUserQuestionStatRepository) {
        this.kbUserQuestionStatRepository = kbUserQuestionStatRepository;
    }

    @Override
    public PageResult<KbExamWrongView> wrongBook(Long userId, String category, Integer pageNum, Integer pageSize) {
        int safePage = ObjectUtil.isNull(pageNum) || pageNum < 1 ? DEFAULT_PAGE_NUM : pageNum;
        int safeSize = ObjectUtil.isNull(pageSize) || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
        int offset = (safePage - 1) * safeSize;
        String safeCategory = StrUtil.trimToNull(category);

        List<KbExamWrongView> list = kbUserQuestionStatRepository.findWrongBook(userId, safeCategory, offset, safeSize);
        long total = kbUserQuestionStatRepository.countWrongBook(userId, safeCategory);
        return new PageResult<>(total, safePage, safeSize, list);
    }

    @Override
    public void markMastered(Long userId, Long questionId) {
        int affected = kbUserQuestionStatRepository.removeFromWrongBook(userId, questionId);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.PARAM_INVALID, "该题目不在你的错题集中");
    }
}
