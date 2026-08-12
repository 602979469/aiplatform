package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysDictTypeManager;
import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysDictTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典类型管理实现类
 *
 */
@Service
public class SysDictTypeManagerImpl implements SysDictTypeManager {

    /** 字典类型领域服务。 */
    private final SysDictTypeService sysDictTypeService;

    public SysDictTypeManagerImpl(SysDictTypeService sysDictTypeService) {
        this.sysDictTypeService = sysDictTypeService;
    }

    @Override
    public SysDictType createSysDictType(SysDictType sysDictType) {
        SysDictType created = sysDictTypeService.createSysDictType(sysDictType);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建字典类型成功 dictId={}", created.getDictId());
        return created;
    }

    @Override
    public SysDictType getSysDictType(Long id) {
        return sysDictTypeService.getSysDictType(id);
    }

    @Override
    public PageResult<SysDictType> pageSysDictTypes(SysDictTypeQueryParam query) {
        return sysDictTypeService.findPage(query);
    }

    @Override
    public List<SysDictType> listSysDictTypes(SysDictTypeQueryParam query) {
        return sysDictTypeService.findList(query);
    }

    @Override
    public void updateSysDictType(SysDictType sysDictType) {
        sysDictTypeService.updateSysDictType(sysDictType);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新字典类型成功 dictId={}", sysDictType.getDictId());
    }

    @Override
    public void updateByCondition(SysDictType sysDictType) {
        sysDictTypeService.updateByCondition(sysDictType);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新字典类型成功 dictId={}", sysDictType.getDictId());
    }

    @Override
    public void deleteSysDictType(Long id) {
        sysDictTypeService.deleteSysDictType(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除字典类型成功 id={}", id);
    }
}
