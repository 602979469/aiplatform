package com.jakt.aiplatform.core.repository.impl;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.KbUserQuestionStatDO;
import com.jakt.aiplatform.common.dal.mapper.KbUserQuestionStatMapper;
import com.jakt.aiplatform.common.dal.query.KbUserQuestionStatDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.core.model.dto.KbExamWrongView;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatDelta;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatQueryParam;
import com.jakt.aiplatform.core.repository.KbUserQuestionStatRepository;
import com.jakt.aiplatform.core.repository.convertor.KbUserQuestionStatConvertor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户题目掌握状态仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class KbUserQuestionStatRepositoryImpl implements KbUserQuestionStatRepository {

    /** 用户题目掌握状态 Mapper。 */
    private final KbUserQuestionStatMapper kbUserQuestionStatMapper;

    public KbUserQuestionStatRepositoryImpl(KbUserQuestionStatMapper kbUserQuestionStatMapper) {
        this.kbUserQuestionStatMapper = kbUserQuestionStatMapper;
    }

    @Override
    public KbUserQuestionStat findById(Long id) {
        KbUserQuestionStatDO kbUserQuestionStatDO = kbUserQuestionStatMapper.selectById(id);
        return KbUserQuestionStatConvertor.toModel(kbUserQuestionStatDO);
    }

    @Override
    public List<KbUserQuestionStat> findList(KbUserQuestionStatQueryParam query) {
        KbUserQuestionStatDalQuery dalQuery = KbUserQuestionStatConvertor.toDalQuery(query);
        List<KbUserQuestionStatDO> doList = kbUserQuestionStatMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, KbUserQuestionStatConvertor::toModel);
    }

    @Override
    public KbUserQuestionStat findOne(KbUserQuestionStatQueryParam query) {
        KbUserQuestionStatDalQuery dalQuery = KbUserQuestionStatConvertor.toDalQuery(query);
        KbUserQuestionStatDO row = kbUserQuestionStatMapper.selectOne(dalQuery);
        return ObjectUtil.isNull(row) ? null : KbUserQuestionStatConvertor.toModel(row);
    }

    @Override
    public PageResult<KbUserQuestionStat> findPage(KbUserQuestionStatQueryParam query) {
        KbUserQuestionStatDalQuery dalQuery = KbUserQuestionStatConvertor.toDalQuery(query);
        List<KbUserQuestionStatDO> doList = kbUserQuestionStatMapper.selectPage(dalQuery);
        long total = kbUserQuestionStatMapper.countByQuery(dalQuery);
        List<KbUserQuestionStat> list = ConvertUtil.map(doList, KbUserQuestionStatConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public KbUserQuestionStat insert(KbUserQuestionStat kbUserQuestionStat) {
        KbUserQuestionStatDO kbUserQuestionStatDO = KbUserQuestionStatConvertor.toDO(kbUserQuestionStat);
        kbUserQuestionStatMapper.insert(kbUserQuestionStatDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        kbUserQuestionStat.setId(kbUserQuestionStatDO.getId());
        return kbUserQuestionStat;
    }

    @Override
    public int update(KbUserQuestionStat kbUserQuestionStat) {
        KbUserQuestionStatDO kbUserQuestionStatDO = KbUserQuestionStatConvertor.toDO(kbUserQuestionStat);
        int affected = kbUserQuestionStatMapper.update(kbUserQuestionStatDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbUserQuestionStatRepository.update id={} 影响行数={}",
                kbUserQuestionStat.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbUserQuestionStat kbUserQuestionStat) {
        int affected = kbUserQuestionStatMapper.updateByCondition(KbUserQuestionStatConvertor.toDO(kbUserQuestionStat));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbUserQuestionStatRepository.updateByCondition id={} 影响行数={}",
                kbUserQuestionStat.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = kbUserQuestionStatMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbUserQuestionStatRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }

    @Override
    public List<KbExamWrongView> findWrongBook(Long userId, String category, int offset, int limit) {
        List<Map<String, Object>> rows = kbUserQuestionStatMapper.selectWrongBook(userId, category, limit, offset);
        List<KbExamWrongView> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            KbExamWrongView item = new KbExamWrongView();
            item.setQuestionId(toLong(row.get("question_id")));
            item.setTitle(toStr(row.get("title")));
            item.setCategory(toStr(row.get("category")));
            item.setSubtopic(toStr(row.get("subtopic")));
            item.setQuestionType(toStr(row.get("question_type")));
            item.setAnswer(toStr(row.get("answer")));
            item.setUserAnswer(toStr(row.get("user_answer")));
            item.setExplanation(toStr(row.get("explanation")));
            item.setWrongCount(toInt(row.get("wrong_count")));
            item.setLastAnswerTime(toDateTime(row.get("last_answer_time")));
            list.add(item);
        }
        return list;
    }

    @Override
    public long countWrongBook(Long userId, String category) {
        return kbUserQuestionStatMapper.countWrongBook(userId, category);
    }

    @Override
    public int removeFromWrongBook(Long userId, Long questionId) {
        int affected = kbUserQuestionStatMapper.updateWrongBookFlag(userId, questionId, 1, 0);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbUserQuestionStatRepository.removeFromWrongBook userId={} questionId={} 影响行数={}",
                userId, questionId, affected);
        return affected;
    }

    @Override
    public int upsertStat(KbUserQuestionStatDelta delta) {
        return kbUserQuestionStatMapper.upsertStat(KbUserQuestionStatConvertor.toDelta(delta));
    }

    /**
     * Map 取值转 Long。
     *
     * @param value 原始值
     * @return Long 值；为空返回 null
     */
    private Long toLong(Object value) {
        if (ObjectUtil.isNull(value)) {
            return null;
        }
        return Long.valueOf(String.valueOf(value));
    }

    /**
     * Map 取值转 Integer。
     *
     * @param value 原始值
     * @return Integer 值；为空返回 null
     */
    private Integer toInt(Object value) {
        if (ObjectUtil.isNull(value)) {
            return null;
        }
        return Integer.valueOf(String.valueOf(value));
    }

    /**
     * Map 取值转字符串。
     *
     * @param value 原始值
     * @return 字符串；为空返回 null
     */
    private String toStr(Object value) {
        return ObjectUtil.isNull(value) ? null : String.valueOf(value);
    }

    /**
     * Map 取值转 LocalDateTime。
     *
     * @param value 原始值
     * @return 时间；为空返回 null
     */
    private LocalDateTime toDateTime(Object value) {
        if (ObjectUtil.isNull(value)) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }
        return LocalDateTime.parse(String.valueOf(value).replace(" ", "T"));
    }
}
