package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysLogininforDO;
import com.jakt.aiplatform.common.dal.mapper.SysLogininforMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysLogininforQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysLogininforRepository;
import com.jakt.aiplatform.core.repository.convertor.SysLogininforConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 登录日志仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysLogininforRepositoryImpl implements SysLogininforRepository {

    /** 登录日志 Mapper。 */
    private final SysLogininforMapper sysLogininforMapper;

    public SysLogininforRepositoryImpl(SysLogininforMapper sysLogininforMapper) {
        this.sysLogininforMapper = sysLogininforMapper;
    }

    @Override
    public SysLogininfor findById(Long id) {
        return SysLogininforConvertor.toModel(sysLogininforMapper.selectById(id));
    }

    @Override
    public List<SysLogininfor> findList(SysLogininforQueryParam query) {
        return sysLogininforMapper.selectList(query).stream().map(SysLogininforConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysLogininfor> findPage(SysLogininforQueryParam query) {
        List<SysLogininforDO> doList = sysLogininforMapper.selectPage(query);
        long total = sysLogininforMapper.countByQuery(query);
        List<SysLogininfor> list = doList.stream().map(SysLogininforConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysLogininfor insert(SysLogininfor sysLogininfor) {
        SysLogininforDO sysLogininforDO = SysLogininforConvertor.toDO(sysLogininfor);
        sysLogininforMapper.insert(sysLogininforDO);
        return SysLogininforConvertor.toModel(sysLogininforDO);
    }

    @Override
    public void update(SysLogininfor sysLogininfor) {
        SysLogininforDO sysLogininforDO = SysLogininforConvertor.toDO(sysLogininfor);
        int affected = sysLogininforMapper.update(sysLogininforDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysLogininforRepository.update infoId={} 影响行数={}", sysLogininfor.getInfoId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysLogininfor sysLogininfor) {
        int affected = sysLogininforMapper.updateByCondition(SysLogininforConvertor.toDO(sysLogininfor));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysLogininforRepository.updateByCondition infoId={} 影响行数={}", sysLogininfor.getInfoId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysLogininforMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysLogininforRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
