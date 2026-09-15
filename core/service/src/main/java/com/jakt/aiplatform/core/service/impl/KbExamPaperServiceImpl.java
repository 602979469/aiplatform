package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.param.KbExamPaperQueryParam;
import com.jakt.aiplatform.core.repository.KbExamPaperRepository;
import com.jakt.aiplatform.core.service.KbExamPaperService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考试试卷领域服务实现：承载考试试卷相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class KbExamPaperServiceImpl implements KbExamPaperService {

    /** 考试试卷仓储。 */
    private final KbExamPaperRepository kbExamPaperRepository;

    public KbExamPaperServiceImpl(KbExamPaperRepository kbExamPaperRepository) {
        this.kbExamPaperRepository = kbExamPaperRepository;
    }

    @Override
    public KbExamPaper createKbExamPaper(KbExamPaper kbExamPaper) {
        return kbExamPaperRepository.insert(kbExamPaper);
    }

    @Override
    public int updateKbExamPaper(KbExamPaper kbExamPaper) {
        return kbExamPaperRepository.update(kbExamPaper);
    }

    @Override
    public int updateByCondition(KbExamPaper kbExamPaper) {
        return kbExamPaperRepository.updateByCondition(kbExamPaper);
    }

    @Override
    public int deleteKbExamPaper(Long id) {
        return kbExamPaperRepository.deleteById(id);
    }

    @Override
    public KbExamPaper getKbExamPaper(Long id) {
        return kbExamPaperRepository.findById(id);
    }

    @Override
    public PageResult<KbExamPaper> findPage(KbExamPaperQueryParam query) {
        return kbExamPaperRepository.findPage(query);
    }

    @Override
    public List<KbExamPaper> findList(KbExamPaperQueryParam query) {
        return kbExamPaperRepository.findList(query);
    }
}
