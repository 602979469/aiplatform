package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.HomePurchaseItemDO;
import com.jakt.aiplatform.common.dal.mapper.HomePurchaseItemMapper;
import com.jakt.aiplatform.common.dal.query.HomePurchaseItemDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;
import com.jakt.aiplatform.core.repository.HomePurchaseItemRepository;
import com.jakt.aiplatform.core.repository.convertor.HomePurchaseItemConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家庭装修采购项仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class HomePurchaseItemRepositoryImpl implements HomePurchaseItemRepository {

    /** 家庭装修采购项 Mapper。 */
    private final HomePurchaseItemMapper homePurchaseItemMapper;

    public HomePurchaseItemRepositoryImpl(HomePurchaseItemMapper homePurchaseItemMapper) {
        this.homePurchaseItemMapper = homePurchaseItemMapper;
    }

    @Override
    public HomePurchaseItem findById(Long id) {
        HomePurchaseItemDO homePurchaseItemDO = homePurchaseItemMapper.selectById(id);
        return HomePurchaseItemConvertor.toModel(homePurchaseItemDO);
    }

    @Override
    public List<HomePurchaseItem> findList(HomePurchaseItemQueryParam query) {
        HomePurchaseItemDalQuery dalQuery = HomePurchaseItemConvertor.toDalQuery(query);
        List<HomePurchaseItemDO> doList = homePurchaseItemMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, HomePurchaseItemConvertor::toModel);
    }

    @Override
    public HomePurchaseItem findOne(HomePurchaseItemQueryParam query) {
        HomePurchaseItemDalQuery dalQuery = HomePurchaseItemConvertor.toDalQuery(query);
        HomePurchaseItemDO row = homePurchaseItemMapper.selectOne(dalQuery);
        return row == null ? null : HomePurchaseItemConvertor.toModel(row);
    }

    @Override
    public PageResult<HomePurchaseItem> findPage(HomePurchaseItemQueryParam query) {
        HomePurchaseItemDalQuery dalQuery = HomePurchaseItemConvertor.toDalQuery(query);
        List<HomePurchaseItemDO> doList = homePurchaseItemMapper.selectPage(dalQuery);
        long total = homePurchaseItemMapper.countByQuery(dalQuery);
        List<HomePurchaseItem> list = ConvertUtil.map(doList, HomePurchaseItemConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public HomePurchaseItem insert(HomePurchaseItem homePurchaseItem) {
        HomePurchaseItemDO homePurchaseItemDO = HomePurchaseItemConvertor.toDO(homePurchaseItem);
        homePurchaseItemMapper.insert(homePurchaseItemDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        homePurchaseItem.setId(homePurchaseItemDO.getId());
        return homePurchaseItem;
    }

    @Override
    public int update(HomePurchaseItem homePurchaseItem) {
        HomePurchaseItemDO homePurchaseItemDO = HomePurchaseItemConvertor.toDO(homePurchaseItem);
        int affected = homePurchaseItemMapper.update(homePurchaseItemDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "HomePurchaseItemRepository.update id={} 影响行数={}",
                homePurchaseItem.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(HomePurchaseItem homePurchaseItem) {
        int affected = homePurchaseItemMapper.updateByCondition(HomePurchaseItemConvertor.toDO(homePurchaseItem));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "HomePurchaseItemRepository.updateByCondition id={} 影响行数={}",
                homePurchaseItem.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = homePurchaseItemMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "HomePurchaseItemRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
