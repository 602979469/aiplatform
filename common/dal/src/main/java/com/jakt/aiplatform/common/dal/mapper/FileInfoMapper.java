package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.FileInfoDO;
import com.jakt.aiplatform.common.dal.query.FileInfoDalQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文件信息表 Mapper。SQL 全部在 resources/mapper/FileInfoMapper.xml 中；
 * 查询参数使用 common-dal 的 FileInfoDalQuery，common-dal 不依赖 core-model。
 */
@Mapper
public interface FileInfoMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 文件信息表数据对象
     */
    FileInfoDO selectById(Long id);

    /**
     * 按主键查询文件内容（列表查询不加载大字段）。
     *
     * @param id 主键
     * @return 数据对象（仅 fileContent 有值）；不存在返回 null
     */
    FileInfoDO selectContentById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<FileInfoDO> selectPage(FileInfoDalQuery query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(FileInfoDalQuery query);

    /**
     * 新增，返回受影响行数；自增主键回填到入参 DO。
     *
     * @param fileInfoDO 数据对象
     * @return 受影响行数
     */
    int insert(FileInfoDO fileInfoDO);

    /**
     * 按主键全量更新，返回受影响行数。
     *
     * @param fileInfoDO 数据对象（含主键）
     * @return 受影响行数
     */
    int update(FileInfoDO fileInfoDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），用于元信息修改，不更新文件内容。
     *
     * @param fileInfoDO 数据对象（含主键）
     * @return 受影响行数
     */
    int updateByCondition(FileInfoDO fileInfoDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);
}
