package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.biz.service.KbUserQuestionStatManager;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatQueryParam;
import com.jakt.aiplatform.core.service.KbUserQuestionStatService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户题目掌握状态管理实现类
 */
@Service
public class KbUserQuestionStatManagerImpl implements KbUserQuestionStatManager {

    /** 用户题目掌握状态领域服务。 */
    private final KbUserQuestionStatService kbUserQuestionStatService;

    public KbUserQuestionStatManagerImpl(KbUserQuestionStatService kbUserQuestionStatService) {
        this.kbUserQuestionStatService = kbUserQuestionStatService;
    }

    @Override
    public KbUserQuestionStat createKbUserQuestionStat(KbUserQuestionStat kbUserQuestionStat) {
        KbUserQuestionStat created = kbUserQuestionStatService.createKbUserQuestionStat(kbUserQuestionStat);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建用户题目掌握状态成功 id={}", created.getId());
        return created;
    }

    @Override
    public KbUserQuestionStat getKbUserQuestionStat(Long id) {
        return kbUserQuestionStatService.getKbUserQuestionStat(id);
    }

    @Override
    public PageResult<KbUserQuestionStat> pageKbUserQuestionStats(KbUserQuestionStatQueryParam query) {
        return kbUserQuestionStatService.findPage(query);
    }

    @Override
    public List<KbUserQuestionStat> listKbUserQuestionStats(KbUserQuestionStatQueryParam query) {
        return kbUserQuestionStatService.findList(query);
    }

    @Override
    public int updateKbUserQuestionStat(KbUserQuestionStat kbUserQuestionStat) {
        int affected = kbUserQuestionStatService.updateKbUserQuestionStat(kbUserQuestionStat);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新用户题目掌握状态成功 id={} 影响行数={}",
                kbUserQuestionStat.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbUserQuestionStat kbUserQuestionStat) {
        int affected = kbUserQuestionStatService.updateByCondition(kbUserQuestionStat);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新用户题目掌握状态成功 id={} 影响行数={}",
                kbUserQuestionStat.getId(), affected);
        return affected;
    }

    @Override
    public int deleteKbUserQuestionStat(Long id) {
        int affected = kbUserQuestionStatService.deleteKbUserQuestionStat(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除用户题目掌握状态成功 id={} 影响行数={}", id, affected);
        return affected;
    }
}
