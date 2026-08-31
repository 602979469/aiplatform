package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.ClusterImageDO;
import com.jakt.aiplatform.common.dal.mapper.ClusterImageMapper;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;
import com.jakt.aiplatform.core.repository.ClusterImageRepository;
import com.jakt.aiplatform.core.repository.convertor.ClusterImageConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 镜像表仓储实现。
 */
@Repository
public class ClusterImageRepositoryImpl implements ClusterImageRepository {

    private final ClusterImageMapper clusterImageMapper;

    public ClusterImageRepositoryImpl(ClusterImageMapper clusterImageMapper) {
        this.clusterImageMapper = clusterImageMapper;
    }

    @Override
    public ClusterImage findById(Long id) {
        return ClusterImageConvertor.toModel(clusterImageMapper.selectById(id));
    }

    @Override
    public ClusterImage findOne(ClusterImageQueryParam query) {
        List<ClusterImageDO> list = clusterImageMapper.selectList(ClusterImageConvertor.toDalQuery(query));
        AssertUtil.throwErrWhenTrue(list.size() > 1, BizErrorCodeEnum.RESULT_NOT_UNIQUE, "镜像记录不唯一");
        return list.isEmpty() ? null : ClusterImageConvertor.toModel(list.get(0));
    }

    @Override
    public PageResult<ClusterImage> findPage(ClusterImageQueryParam query) {
        com.jakt.aiplatform.common.dal.query.ClusterImageDalQuery dalQuery = ClusterImageConvertor.toDalQuery(query);
        List<ClusterImageDO> list = clusterImageMapper.selectPage(dalQuery);
        long total = clusterImageMapper.countByQuery(dalQuery);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(),
                ConvertUtil.map(list, ClusterImageConvertor::toModel));
    }

    @Override
    public List<ClusterImage> findList(ClusterImageQueryParam query) {
        return ConvertUtil.map(clusterImageMapper.selectList(ClusterImageConvertor.toDalQuery(query)),
                ClusterImageConvertor::toModel);
    }

    @Override
    public ClusterImage insert(ClusterImage image) {
        ClusterImageDO imageDO = ClusterImageConvertor.toDO(image);
        clusterImageMapper.insert(imageDO);
        image.setId(imageDO.getId());
        return image;
    }

    @Override
    public int update(ClusterImage image) {
        return clusterImageMapper.update(ClusterImageConvertor.toDO(image));
    }

    @Override
    public int updateByCondition(ClusterImage image) {
        return clusterImageMapper.updateByCondition(ClusterImageConvertor.toDO(image));
    }

    @Override
    public int deleteById(Long id) {
        return clusterImageMapper.deleteById(id);
    }
}
