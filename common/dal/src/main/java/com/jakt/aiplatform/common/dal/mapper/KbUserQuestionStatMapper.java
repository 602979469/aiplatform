package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.KbUserQuestionStatDO;
import com.jakt.aiplatform.common.dal.query.KbUserQuestionStatDalQuery;
import com.jakt.aiplatform.common.dal.query.KbQuestionStatDelta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 用户题目掌握状态 Mapper。SQL 全部在 resources/mapper/KbUserQuestionStatMapper.xml 中；
 * 查询参数使用 common-dal 的 KbUserQuestionStatDalQuery，common-dal 不依赖 core-model。
 */
@Mapper
public interface KbUserQuestionStatMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 用户题目掌握状态数据对象
     */
    KbUserQuestionStatDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<KbUserQuestionStatDO> selectPage(KbUserQuestionStatDalQuery query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<KbUserQuestionStatDO> selectList(KbUserQuestionStatDalQuery query);

    /**
     * 单条查询：与 {@link #selectList} 一致但不加 LIMIT；多条由 MyBatis 抛 TooManyResultsException，不做特殊处理。
     *
     * @param query 查询参数
     * @return 至多一条数据，无匹配返回 null
     */
    KbUserQuestionStatDO selectOne(KbUserQuestionStatDalQuery query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(KbUserQuestionStatDalQuery query);

    /**
     * 新增，返回受影响行数；自增主键回填到入参 DO。
     *
     * @param kbUserQuestionStatDO 数据对象
     * @return 受影响行数
     */
    int insert(KbUserQuestionStatDO kbUserQuestionStatDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param kbUserQuestionStatDO 数据对象
     * @return 受影响行数
     */
    int update(KbUserQuestionStatDO kbUserQuestionStatDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；
     * create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param kbUserQuestionStatDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(KbUserQuestionStatDO kbUserQuestionStatDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 掌握度增量 upsert：答对/答错累加次数，并更新最近结果、掌握标记与错题集标记。
     *
     * @param delta 增量参数
     * @return 影响行数
     */
    int upsertStat(KbQuestionStatDelta delta);

    /**
     * 错题集列表：掌握度表关联题库，返回错题明细（我的答案/正确答案/解析）。
     *
     * @param userId 用户ID
     * @param category 分类（可空）
     * @param limit 每页条数
     * @param offset 偏移量
     * @return 每行包含 question_id / title / category / subtopic / question_type / answer /
     *         user_answer / explanation / wrong_count / last_answer_time
     */
    List<Map<String, Object>> selectWrongBook(@Param("userId") Long userId,
                                              @Param("category") String category,
                                              @Param("limit") int limit,
                                              @Param("offset") int offset);

    /**
     * 统计错题集条数。
     *
     * @param userId 用户ID
     * @param category 分类（可空）
     * @return 条数
     */
    long countWrongBook(@Param("userId") Long userId,
                        @Param("category") String category);

    /**
     * 更新错题集标记（标记已掌握 = 移出错题集）。
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param mastered 是否已掌握（1是）
     * @param inWrongBook 是否在错题集（0移出）
     * @return 影响行数
     */
    int updateWrongBookFlag(@Param("userId") Long userId,
                            @Param("questionId") Long questionId,
                            @Param("mastered") Integer mastered,
                            @Param("inWrongBook") Integer inWrongBook);
}
