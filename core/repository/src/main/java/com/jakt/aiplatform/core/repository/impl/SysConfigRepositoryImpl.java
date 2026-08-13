package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysConfigDO;
import com.jakt.aiplatform.common.dal.mapper.SysConfigMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import com.jakt.aiplatform.core.repository.SysConfigRepository;
import com.jakt.aiplatform.core.repository.convertor.SysConfigConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 参数配置仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysConfigRepositoryImpl implements SysConfigRepository {

    /** 参数配置 Mapper。 */
    private final SysConfigMapper sysConfigMapper;

    public SysConfigRepositoryImpl(SysConfigMapper sysConfigMapper) {
        this.sysConfigMapper = sysConfigMapper;
    }

    private SysConfig findOne(SysConfigDO sysConfigDO) {
        SysConfigQueryParam query = SysConfigConvertor.toQueryParam(sysConfigDO);
        List<SysConfigDO> list = sysConfigMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysConfigConvertor.toModel(list.get(0));
    }

    @Override
    public SysConfig selectConfig(SysConfig config) {
        SysConfigDO condition = new SysConfigDO();
        condition.setConfigKey(config.getConfigKey());
        return findOne(condition);
    }

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        List<SysConfigDO> list = sysConfigMapper.selectList(SysConfigConvertor.toQueryParam(config));
        return ListUtil.convert(list, SysConfigConvertor::toModel);
    }

    @Override
    public SysConfig selectConfigById(Long configId) {
        return SysConfigConvertor.toModel(sysConfigMapper.selectById(configId));
    }

    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        SysConfigQueryParam query = new SysConfigQueryParam();
        query.setConfigKey(config.getConfigKey());
        List<SysConfigDO> list = sysConfigMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getConfigId(), config.getConfigId());
    }

    @Override
    public int insertConfig(SysConfig config) {
        return sysConfigMapper.insert(SysConfigConvertor.toDO(config));
    }

    @Override
    public int updateConfig(SysConfig config) {
        return sysConfigMapper.update(SysConfigConvertor.toDO(config));
    }

    @Override
    public int deleteConfigById(Long configId) {
        return sysConfigMapper.deleteById(configId);
    }

    @Override
    public int deleteConfigByIds(String ids) {
        return sysConfigMapper.deleteByIds(Convert.toLongArray(ids));
    }
}
