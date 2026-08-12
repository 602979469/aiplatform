package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysDictDataManager;
import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysDictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据管理实现类
 *
 */
@Service
public class SysDictDataManagerImpl implements SysDictDataManager {

    /** 字典数据领域服务。 */
    private final SysDictDataService sysDictDataService;

    public SysDictDataManagerImpl(SysDictDataService sysDictDataService) {
        this.sysDictDataService = sysDictDataService;
    }

    @Override
    public SysDictData createSysDictData(SysDictData sysDictData) {
        SysDictData created = sysDictDataService.createSysDictData(sysDictData);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建字典数据成功 dictCode={}", created.getDictCode());
        return created;
    }

    @Override
    public SysDictData getSysDictData(Long id) {
        return sysDictDataService.getSysDictData(id);
    }

    @Override
    public PageResult<SysDictData> pageSysDictDatas(SysDictDataQueryParam query) {
        return sysDictDataService.findPage(query);
    }

    @Override
    public List<SysDictData> listSysDictDatas(SysDictDataQueryParam query) {
        return sysDictDataService.findList(query);
    }

    @Override
    public void updateSysDictData(SysDictData sysDictData) {
        sysDictDataService.updateSysDictData(sysDictData);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新字典数据成功 dictCode={}", sysDictData.getDictCode());
    }

    @Override
    public void updateByCondition(SysDictData sysDictData) {
        sysDictDataService.updateByCondition(sysDictData);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新字典数据成功 dictCode={}", sysDictData.getDictCode());
    }

    @Override
    public void deleteSysDictData(Long id) {
        sysDictDataService.deleteSysDictData(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除字典数据成功 id={}", id);
    }
}
