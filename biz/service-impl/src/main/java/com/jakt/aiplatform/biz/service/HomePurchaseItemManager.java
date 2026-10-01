package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeGroup;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.dto.ProductSuggestionView;
import com.jakt.aiplatform.core.model.dto.PurchaseItemDraftView;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;

import java.util.List;

/**
 * 家庭装修采购项管理：web 层唯一入口，负责编排领域服务与业务日志。
 */
public interface HomePurchaseItemManager {

    /**
     * 创建采购项。
     *
     * @param homePurchaseItem 采购项
     * @return 创建后的采购项
     */
    HomePurchaseItem createHomePurchaseItem(HomePurchaseItem homePurchaseItem);

    /**
     * 按主键查询（含图片）。
     *
     * @param id 主键
     * @return 采购项；不存在返回 null
     */
    HomePurchaseItem getHomePurchaseItem(Long id);

    /**
     * 分页查询（含图片）。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<HomePurchaseItem> pageHomePurchaseItems(HomePurchaseItemQueryParam query);

    /**
     * 列表查询（含图片），供预算报告页一次拉取全部采购项。
     *
     * @param query 查询参数
     * @return 采购项列表
     */
    List<HomePurchaseItem> listHomePurchaseItems(HomePurchaseItemQueryParam query);

    /**
     * 全量更新采购项。
     *
     * @param homePurchaseItem 采购项（含主键）
     * @return 受影响行数
     */
    int updateHomePurchaseItem(HomePurchaseItem homePurchaseItem);

    /**
     * 删除采购项（连同图片）。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteHomePurchaseItem(Long id);

    /**
     * 家具类型树（大类 → 小类），供移动端/电脑端下拉框使用。
     *
     * @return 类型树
     */
    List<FurnitureTypeGroup> listFurnitureTypes();

    /**
     * AI 推荐 3 款候选产品。
     *
     * @param bigTypeCode 大类编码
     * @param typeCode 小类编码
     * @param budgetText 预算原文，可为空
     * @param remark 补充说明，可为空
     * @return 候选产品列表
     */
    List<ProductSuggestionView> recommendProducts(String bigTypeCode, String typeCode,
                                                  String budgetText, String remark);

    /**
     * 一句话录入：把用户口语转成采购项草稿（不落库，等用户确认）。
     *
     * @param text 用户原话
     * @return 解析草稿
     */
    PurchaseItemDraftView parseByText(String text);
}
