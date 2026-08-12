package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.GenTableColumnManager;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.GenTableColumnService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代码生成字段管理实现类
 *
 */
@Service
public class GenTableColumnManagerImpl implements GenTableColumnManager {

    /** 代码生成字段领域服务。 */
    private final GenTableColumnService genTableColumnService;

    public GenTableColumnManagerImpl(GenTableColumnService genTableColumnService) {
        this.genTableColumnService = genTableColumnService;
    }

    @Override
    public GenTableColumn createGenTableColumn(GenTableColumn genTableColumn) {
        GenTableColumn created = genTableColumnService.createGenTableColumn(genTableColumn);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建代码生成字段成功 columnId={}", created.getColumnId());
        return created;
    }

    @Override
    public GenTableColumn getGenTableColumn(Long id) {
        return genTableColumnService.getGenTableColumn(id);
    }

    @Override
    public PageResult<GenTableColumn> pageGenTableColumns(GenTableColumnQueryParam query) {
        return genTableColumnService.findPage(query);
    }

    @Override
    public List<GenTableColumn> listGenTableColumns(GenTableColumnQueryParam query) {
        return genTableColumnService.findList(query);
    }

    @Override
    public void updateGenTableColumn(GenTableColumn genTableColumn) {
        genTableColumnService.updateGenTableColumn(genTableColumn);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新代码生成字段成功 columnId={}", genTableColumn.getColumnId());
    }

    @Override
    public void updateByCondition(GenTableColumn genTableColumn) {
        genTableColumnService.updateByCondition(genTableColumn);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新代码生成字段成功 columnId={}", genTableColumn.getColumnId());
    }

    @Override
    public void deleteGenTableColumn(Long id) {
        genTableColumnService.deleteGenTableColumn(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除代码生成字段成功 id={}", id);
    }
}
