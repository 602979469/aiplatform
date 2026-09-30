package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.HomePurchaseItemImageDO;
import com.jakt.aiplatform.common.dal.mapper.HomePurchaseItemImageMapper;
import com.jakt.aiplatform.common.dal.query.HomePurchaseItemImageDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItemImage;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemImageQueryParam;
import com.jakt.aiplatform.core.repository.HomePurchaseItemImageRepository;
import com.jakt.aiplatform.core.repository.convertor.HomePurchaseItemImageConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家庭装修采购项图片仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class HomePurchaseItemImageRepositoryImpl implements HomePurchaseItemImageRepository {

    /** 家庭装修采购项图片 Mapper。 */
    private final HomePurchaseItemImageMapper homePurchaseItemImageMapper;

    public HomePurchaseItemImageRepositoryImpl(HomePurchaseItemImageMapper homePurchaseItemImageMapper) {
        this.homePurchaseItemImageMapper = homePurchaseItemImageMapper;
    }

    @Override
    public List<HomePurchaseItemImage> findByItemId(Long itemId) {
        HomePurchaseItemImageQueryParam query = new HomePurchaseItemImageQueryParam();
        query.setItemId(itemId);
        return findList(query);
    }

    @Override
    public List<HomePurchaseItemImage> findList(HomePurchaseItemImageQueryParam query) {
        HomePurchaseItemImageDalQuery dalQuery = HomePurchaseItemImageConvertor.toDalQuery(query);
        List<HomePurchaseItemImageDO> doList = homePurchaseItemImageMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, HomePurchaseItemImageConvertor::toModel);
    }

    @Override
    public HomePurchaseItemImage insert(HomePurchaseItemImage homePurchaseItemImage) {
        HomePurchaseItemImageDO homePurchaseItemImageDO = HomePurchaseItemImageConvertor.toDO(homePurchaseItemImage);
        homePurchaseItemImageMapper.insert(homePurchaseItemImageDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        homePurchaseItemImage.setId(homePurchaseItemImageDO.getId());
        return homePurchaseItemImage;
    }

    @Override
    public int deleteByItemId(Long itemId) {
        int affected = homePurchaseItemImageMapper.deleteByItemId(itemId);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "HomePurchaseItemImageRepository.deleteByItemId itemId={} 影响行数={}",
                itemId, affected);
        return affected;
    }
}
