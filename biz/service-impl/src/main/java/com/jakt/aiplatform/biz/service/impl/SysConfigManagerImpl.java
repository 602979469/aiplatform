package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysConfigManager;
import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参数配置管理实现类
 *
 */
@Service
public class SysConfigManagerImpl implements SysConfigManager {

    /** 参数配置领域服务。 */
    private final SysConfigService sysConfigService;

    public SysConfigManagerImpl(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    @Override
    public SysConfig createSysConfig(SysConfig sysConfig) {
        SysConfig created = sysConfigService.createSysConfig(sysConfig);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建参数配置成功 configId={}", created.getConfigId());
        return created;
    }

    @Override
    public SysConfig getSysConfig(Long id) {
        return sysConfigService.getSysConfig(id);
    }

    @Override
    public PageResult<SysConfig> pageSysConfigs(SysConfigQueryParam query) {
        return sysConfigService.findPage(query);
    }

    @Override
    public List<SysConfig> listSysConfigs(SysConfigQueryParam query) {
        return sysConfigService.findList(query);
    }

    @Override
    public void updateSysConfig(SysConfig sysConfig) {
        sysConfigService.updateSysConfig(sysConfig);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新参数配置成功 configId={}", sysConfig.getConfigId());
    }

    @Override
    public void updateByCondition(SysConfig sysConfig) {
        sysConfigService.updateByCondition(sysConfig);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新参数配置成功 configId={}", sysConfig.getConfigId());
    }

    @Override
    public void deleteSysConfig(Long id) {
        sysConfigService.deleteSysConfig(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除参数配置成功 id={}", id);
    }
}
