package com.jakt.aiplatform.core.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.FileInfoDO;
import com.jakt.aiplatform.common.dal.mapper.FileInfoMapper;
import com.jakt.aiplatform.common.dal.query.FileInfoDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import com.jakt.aiplatform.core.repository.convertor.FileInfoConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文件信息表仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class FileInfoRepositoryImpl implements FileInfoRepository {

    /** 文件信息表 Mapper。 */
    private final FileInfoMapper fileInfoMapper;

    public FileInfoRepositoryImpl(FileInfoMapper fileInfoMapper) {
        this.fileInfoMapper = fileInfoMapper;
    }

    @Override
    public FileInfo findById(Long id) {
        FileInfoDO fileInfoDO = fileInfoMapper.selectById(id);
        return FileInfoConvertor.toModel(fileInfoDO);
    }

    @Override
    public byte[] findContent(Long id) {
        FileInfoDO row = fileInfoMapper.selectContentById(id);
        return ObjectUtil.isNull(row) ? null : row.getFileContent();
    }

    @Override
    public PageResult<FileInfo> findPage(FileInfoQueryParam query) {
        FileInfoDalQuery dalQuery = FileInfoConvertor.toDalQuery(query);
        List<FileInfoDO> doList = fileInfoMapper.selectPage(dalQuery);
        long total = fileInfoMapper.countByQuery(dalQuery);
        List<FileInfo> list = ConvertUtil.map(doList, FileInfoConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public FileInfo insert(FileInfo fileInfo) {
        FileInfoDO fileInfoDO = FileInfoConvertor.toDO(fileInfo);
        fileInfoMapper.insert(fileInfoDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        fileInfo.setId(fileInfoDO.getId());
        return fileInfo;
    }

    @Override
    public int update(FileInfo fileInfo) {
        FileInfoDO fileInfoDO = FileInfoConvertor.toDO(fileInfo);
        int affected = fileInfoMapper.update(fileInfoDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "FileInfoRepository.update id={} 影响行数={}",
                fileInfo.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(FileInfo fileInfo) {
        int affected = fileInfoMapper.updateByCondition(FileInfoConvertor.toDO(fileInfo));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "FileInfoRepository.updateByCondition id={} 影响行数={}",
                fileInfo.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = fileInfoMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "FileInfoRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
