package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.CdcSyncManager;
import com.jakt.aiplatform.core.model.domain.CdcEsMapping;
import com.jakt.aiplatform.core.model.domain.CdcMappingPreview;
import com.jakt.aiplatform.core.model.domain.CdcSyncStatus;
import com.jakt.aiplatform.core.service.CdcSyncService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ES 同步管理实现：只做用例编排，规则与集成调用都在 core-service。
 */
@Service
public class CdcSyncManagerImpl implements CdcSyncManager {

    /** ES 同步配置服务。 */
    private final CdcSyncService cdcSyncService;

    public CdcSyncManagerImpl(CdcSyncService cdcSyncService) {
        this.cdcSyncService = cdcSyncService;
    }

    @Override
    public List<CdcEsMapping> listMappings() {
        return cdcSyncService.listMappings();
    }

    @Override
    public CdcEsMapping getMapping(String name) {
        return cdcSyncService.getMapping(name);
    }

    @Override
    public CdcMappingPreview preview(CdcEsMapping mapping) {
        return cdcSyncService.preview(mapping);
    }

    @Override
    public void saveMapping(CdcEsMapping mapping, Boolean restart) {
        cdcSyncService.saveMapping(mapping, restart);
    }

    @Override
    public void deleteMapping(String name, Boolean restart) {
        cdcSyncService.deleteMapping(name, restart);
    }

    @Override
    public String formatSql(String sql) {
        return cdcSyncService.formatSql(sql);
    }

    @Override
    public String triggerEtl(String name) {
        return cdcSyncService.triggerEtl(name);
    }

    @Override
    public void restartAdapter() {
        cdcSyncService.restartAdapter();
    }

    @Override
    public CdcSyncStatus status() {
        return cdcSyncService.status();
    }

    @Override
    public List<String> listEsIndices() {
        return cdcSyncService.listEsIndices();
    }
}
