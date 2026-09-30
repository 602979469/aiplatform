package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.HomePurchaseItemImage;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemImageQueryParam;

import java.util.List;

/**
 * 家庭装修采购项图片仓储：封装 Mapper，对外只暴露领域模型，不暴露 DO/DalQuery/DalResult。
 */
public interface HomePurchaseItemImageRepository {

    /**
     * 按采购项查询图片（按 order_num 升序）。
     *
     * @param itemId 采购项ID
     * @return 图片列表
     */
    List<HomePurchaseItemImage> findByItemId(Long itemId);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 家庭装修采购项图片列表
     */
    List<HomePurchaseItemImage> findList(HomePurchaseItemImageQueryParam query);

    /**
     * 新增。
     *
     * @param homePurchaseItemImage 家庭装修采购项图片
     * @return 新增后的家庭装修采购项图片；主键已回填到入参，返回同一对象
     */
    HomePurchaseItemImage insert(HomePurchaseItemImage homePurchaseItemImage);

    /**
     * 按采购项删除全部图片。
     *
     * @param itemId 采购项ID
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteByItemId(Long itemId);
}
