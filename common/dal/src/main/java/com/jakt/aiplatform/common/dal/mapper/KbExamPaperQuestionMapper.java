package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.KbExamPaperQuestionDO;
import com.jakt.aiplatform.common.dal.query.KbExamPaperQuestionDalQuery;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 试卷题目快照与作答 Mapper。SQL 全部在 resources/mapper/KbExamPaperQuestionMapper.xml 中；
 * 查询参数使用 common-dal 的 KbExamPaperQuestionDalQuery，common-dal 不依赖 core-model。
 */
@Mapper
public interface KbExamPaperQuestionMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 试卷题目快照与作答数据对象
     */
    KbExamPaperQuestionDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<KbExamPaperQuestionDO> selectPage(KbExamPaperQuestionDalQuery query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<KbExamPaperQuestionDO> selectList(KbExamPaperQuestionDalQuery query);

    /**
     * 单条查询：与 {@link #selectList} 一致但不加 LIMIT；多条由 MyBatis 抛 TooManyResultsException，不做特殊处理。
     *
     * @param query 查询参数
     * @return 至多一条数据，无匹配返回 null
     */
    KbExamPaperQuestionDO selectOne(KbExamPaperQuestionDalQuery query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(KbExamPaperQuestionDalQuery query);

    /**
     * 新增，返回受影响行数；自增主键回填到入参 DO。
     *
     * @param kbExamPaperQuestionDO 数据对象
     * @return 受影响行数
     */
    int insert(KbExamPaperQuestionDO kbExamPaperQuestionDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param kbExamPaperQuestionDO 数据对象
     * @return 受影响行数
     */
    int update(KbExamPaperQuestionDO kbExamPaperQuestionDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；
     * create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param kbExamPaperQuestionDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(KbExamPaperQuestionDO kbExamPaperQuestionDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 按试卷ID删除全部答题明细（删除考试记录时使用）。
     *
     * @param paperId 试卷ID
     * @return 影响行数
     */
    int deleteByPaperId(Long paperId);
}
