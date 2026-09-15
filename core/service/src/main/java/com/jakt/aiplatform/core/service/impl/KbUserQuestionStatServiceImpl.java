package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatQueryParam;
import com.jakt.aiplatform.core.repository.KbUserQuestionStatRepository;
import com.jakt.aiplatform.core.service.KbUserQuestionStatService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户题目掌握状态领域服务实现：承载用户题目掌握状态相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class KbUserQuestionStatServiceImpl implements KbUserQuestionStatService {

    /** 用户题目掌握状态仓储。 */
    private final KbUserQuestionStatRepository kbUserQuestionStatRepository;

    public KbUserQuestionStatServiceImpl(KbUserQuestionStatRepository kbUserQuestionStatRepository) {
        this.kbUserQuestionStatRepository = kbUserQuestionStatRepository;
    }

    @Override
    public KbUserQuestionStat createKbUserQuestionStat(KbUserQuestionStat kbUserQuestionStat) {
        return kbUserQuestionStatRepository.insert(kbUserQuestionStat);
    }

    @Override
    public int updateKbUserQuestionStat(KbUserQuestionStat kbUserQuestionStat) {
        return kbUserQuestionStatRepository.update(kbUserQuestionStat);
    }

    @Override
    public int updateByCondition(KbUserQuestionStat kbUserQuestionStat) {
        return kbUserQuestionStatRepository.updateByCondition(kbUserQuestionStat);
    }

    @Override
    public int deleteKbUserQuestionStat(Long id) {
        return kbUserQuestionStatRepository.deleteById(id);
    }

    @Override
    public KbUserQuestionStat getKbUserQuestionStat(Long id) {
        return kbUserQuestionStatRepository.findById(id);
    }

    @Override
    public PageResult<KbUserQuestionStat> findPage(KbUserQuestionStatQueryParam query) {
        return kbUserQuestionStatRepository.findPage(query);
    }

    @Override
    public List<KbUserQuestionStat> findList(KbUserQuestionStatQueryParam query) {
        return kbUserQuestionStatRepository.findList(query);
    }
}
