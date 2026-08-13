package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysJobDO;
import com.jakt.aiplatform.common.dal.mapper.SysJobMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import com.jakt.aiplatform.core.repository.SysJobRepository;
import com.jakt.aiplatform.core.repository.convertor.SysJobConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 定时任务仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysJobRepositoryImpl implements SysJobRepository {

    /** 定时任务 Mapper。 */
    private final SysJobMapper sysJobMapper;

    public SysJobRepositoryImpl(SysJobMapper sysJobMapper) {
        this.sysJobMapper = sysJobMapper;
    }

    /** 按条件取单条：空返回 null，多条抛 RESULT_NOT_UNIQUE。 */
    private SysJob findOne(SysJobDO condition) {
        List<SysJobDO> list = sysJobMapper.selectList(SysJobConvertor.toQueryParam(condition));
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysJobConvertor.toModel(list.get(0));
    }

    @Override
    public List<SysJob> selectJobList(SysJob job) {
        List<SysJobDO> list = sysJobMapper.selectList(SysJobConvertor.toQueryParam(job));
        return ListUtil.convert(list, SysJobConvertor::toModel);
    }

    @Override
    public List<SysJob> selectJobAll() {
        List<SysJobDO> list = sysJobMapper.selectList(new SysJobQueryParam());
        return ListUtil.convert(list, SysJobConvertor::toModel);
    }

    @Override
    public SysJob selectJobById(Long jobId) {
        SysJobDO condition = new SysJobDO();
        condition.setJobId(jobId);
        return findOne(condition);
    }

    @Override
    public int deleteJobById(Long jobId) {
        return sysJobMapper.deleteById(jobId);
    }

    @Override
    public int deleteJobByIds(String ids) {
        return sysJobMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int updateJob(SysJob job) {
        return sysJobMapper.update(SysJobConvertor.toDO(job));
    }

    @Override
    public int insertJob(SysJob job) {
        return sysJobMapper.insert(SysJobConvertor.toDO(job));
    }
}
