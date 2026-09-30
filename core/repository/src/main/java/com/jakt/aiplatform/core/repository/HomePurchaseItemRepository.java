package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;

import java.util.List;

/**
 * 家庭装修采购项仓储：封装 Mapper，对外只暴露领域模型，不暴露 DO/DalQuery/DalResult。
 */
public interface HomePurchaseItemRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 家庭装修采购项领域模型
     */
    HomePurchaseItem findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<HomePurchaseItem> findPage(HomePurchaseItemQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 家庭装修采购项列表
     */
    List<HomePurchaseItem> findList(HomePurchaseItemQueryParam query);

    /**
     * 按条件查询单条：基于 {@code findList} 的结果集判断，不新增 Mapper 方法。
     *
     * @param query 查询参数
     * @return 家庭装修采购项领域模型；未查询到返回 null，多条由 Mapper selectOne 抛 TooManyResultsException
     */
    HomePurchaseItem findOne(HomePurchaseItemQueryParam query);

    /**
     * 新增。
     *
     * @param homePurchaseItem 家庭装修采购项
     * @return 新增后的家庭装修采购项；主键已回填到入参，返回同一对象
     */
    HomePurchaseItem insert(HomePurchaseItem homePurchaseItem);

    /**
     * 更新（全量）。
     *
     * @param homePurchaseItem 家庭装修采购项（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int update(HomePurchaseItem homePurchaseItem);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param homePurchaseItem 家庭装修采购项（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(HomePurchaseItem homePurchaseItem);

    /**
     * 按主键删除。
     *
     * @param id 主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteById(Long id);
}
