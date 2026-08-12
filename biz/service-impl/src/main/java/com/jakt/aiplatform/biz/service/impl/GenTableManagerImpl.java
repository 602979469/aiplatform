package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.GenTableManager;
import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.GenTableService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代码生成管理实现类
 *
 */
@Service
public class GenTableManagerImpl implements GenTableManager {

    /** 代码生成领域服务。 */
    private final GenTableService genTableService;

    public GenTableManagerImpl(GenTableService genTableService) {
        this.genTableService = genTableService;
    }

    @Override
    public GenTable createGenTable(GenTable genTable) {
        GenTable created = genTableService.createGenTable(genTable);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建代码生成成功 tableId={}", created.getTableId());
        return created;
    }

    @Override
    public GenTable getGenTable(Long id) {
        return genTableService.getGenTable(id);
    }

    @Override
    public PageResult<GenTable> pageGenTables(GenTableQueryParam query) {
        return genTableService.findPage(query);
    }

    @Override
    public List<GenTable> listGenTables(GenTableQueryParam query) {
        return genTableService.findList(query);
    }

    @Override
    public void updateGenTable(GenTable genTable) {
        genTableService.updateGenTable(genTable);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新代码生成成功 tableId={}", genTable.getTableId());
    }

    @Override
    public void updateByCondition(GenTable genTable) {
        genTableService.updateByCondition(genTable);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新代码生成成功 tableId={}", genTable.getTableId());
    }

    @Override
    public void deleteGenTable(Long id) {
        genTableService.deleteGenTable(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除代码生成成功 id={}", id);
    }
}
