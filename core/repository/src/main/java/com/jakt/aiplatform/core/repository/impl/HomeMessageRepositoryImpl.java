package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.HomeMessageDO;
import com.jakt.aiplatform.common.dal.mapper.HomeMessageMapper;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.HomeMessage;
import com.jakt.aiplatform.core.repository.HomeMessageRepository;
import com.jakt.aiplatform.core.repository.convertor.HomeMessageConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 首页留言板留言仓储：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class HomeMessageRepositoryImpl implements HomeMessageRepository {

    /** 首页留言板留言 Mapper。 */
    private final HomeMessageMapper homeMessageMapper;

    public HomeMessageRepositoryImpl(HomeMessageMapper homeMessageMapper) {
        this.homeMessageMapper = homeMessageMapper;
    }

    @Override
    public HomeMessage insert(HomeMessage homeMessage) {
        HomeMessageDO homeMessageDO = HomeMessageConvertor.toDO(homeMessage);
        homeMessageMapper.insert(homeMessageDO);
        return HomeMessageConvertor.toModel(homeMessageDO);
    }

    @Override
    public PageResult<HomeMessage> findPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<HomeMessageDO> doList = homeMessageMapper.selectPage(offset, pageSize);
        PageResult<HomeMessage> result = new PageResult<>();
        result.setTotal(homeMessageMapper.countAll());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setDataList(ConvertUtil.map(doList, HomeMessageConvertor::toModel));
        return result;
    }

    @Override
    public HomeMessage findLatestByIp(String clientIp) {
        return HomeMessageConvertor.toModel(homeMessageMapper.selectLatestByIp(clientIp));
    }
}
