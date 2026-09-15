package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.biz.service.KbExamPaperManager;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.param.KbExamPaperQueryParam;
import com.jakt.aiplatform.core.service.KbExamPaperService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考试试卷管理实现类
 */
@Service
public class KbExamPaperManagerImpl implements KbExamPaperManager {

    /** 考试试卷领域服务。 */
    private final KbExamPaperService kbExamPaperService;

    public KbExamPaperManagerImpl(KbExamPaperService kbExamPaperService) {
        this.kbExamPaperService = kbExamPaperService;
    }

    @Override
    public KbExamPaper createKbExamPaper(KbExamPaper kbExamPaper) {
        KbExamPaper created = kbExamPaperService.createKbExamPaper(kbExamPaper);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建考试试卷成功 id={}", created.getId());
        return created;
    }

    @Override
    public KbExamPaper getKbExamPaper(Long id) {
        return kbExamPaperService.getKbExamPaper(id);
    }

    @Override
    public PageResult<KbExamPaper> pageKbExamPapers(KbExamPaperQueryParam query) {
        return kbExamPaperService.findPage(query);
    }

    @Override
    public List<KbExamPaper> listKbExamPapers(KbExamPaperQueryParam query) {
        return kbExamPaperService.findList(query);
    }

    @Override
    public int updateKbExamPaper(KbExamPaper kbExamPaper) {
        int affected = kbExamPaperService.updateKbExamPaper(kbExamPaper);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新考试试卷成功 id={} 影响行数={}",
                kbExamPaper.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamPaper kbExamPaper) {
        int affected = kbExamPaperService.updateByCondition(kbExamPaper);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新考试试卷成功 id={} 影响行数={}",
                kbExamPaper.getId(), affected);
        return affected;
    }

    @Override
    public int deleteKbExamPaper(Long id) {
        int affected = kbExamPaperService.deleteKbExamPaper(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除考试试卷成功 id={} 影响行数={}", id, affected);
        return affected;
    }
}
