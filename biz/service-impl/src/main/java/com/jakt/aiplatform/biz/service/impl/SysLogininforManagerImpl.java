package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysLogininforManager;
import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysLogininforQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysLogininforService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录日志管理实现类
 *
 */
@Service
public class SysLogininforManagerImpl implements SysLogininforManager {

    /** 登录日志领域服务。 */
    private final SysLogininforService sysLogininforService;

    public SysLogininforManagerImpl(SysLogininforService sysLogininforService) {
        this.sysLogininforService = sysLogininforService;
    }

    @Override
    public SysLogininfor createSysLogininfor(SysLogininfor sysLogininfor) {
        SysLogininfor created = sysLogininforService.createSysLogininfor(sysLogininfor);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建登录日志成功 infoId={}", created.getInfoId());
        return created;
    }

    @Override
    public SysLogininfor getSysLogininfor(Long id) {
        return sysLogininforService.getSysLogininfor(id);
    }

    @Override
    public PageResult<SysLogininfor> pageSysLogininfors(SysLogininforQueryParam query) {
        return sysLogininforService.findPage(query);
    }

    @Override
    public List<SysLogininfor> listSysLogininfors(SysLogininforQueryParam query) {
        return sysLogininforService.findList(query);
    }

    @Override
    public void updateSysLogininfor(SysLogininfor sysLogininfor) {
        sysLogininforService.updateSysLogininfor(sysLogininfor);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新登录日志成功 infoId={}", sysLogininfor.getInfoId());
    }

    @Override
    public void updateByCondition(SysLogininfor sysLogininfor) {
        sysLogininforService.updateByCondition(sysLogininfor);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新登录日志成功 infoId={}", sysLogininfor.getInfoId());
    }

    @Override
    public void deleteSysLogininfor(Long id) {
        sysLogininforService.deleteSysLogininfor(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除登录日志成功 id={}", id);
    }
}
