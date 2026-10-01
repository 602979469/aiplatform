package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.dto.ProductSuggestionView;
import com.jakt.aiplatform.core.model.dto.PurchaseItemDraftView;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;

import java.util.List;

/**
 * 家庭装修采购项领域服务：类型校验与名称快照、预算原文解析、图片编排等业务规则收口于此。
 */
public interface HomePurchaseItemService {

    /**
     * 创建采购项：服务端补全类型名称快照、预算区间与图片，录入人取当前登录用户。
     *
     * @param homePurchaseItem 采购项（含预算原文与图片）
     * @return 创建后的采购项（含图片）
     */
    HomePurchaseItem createHomePurchaseItem(HomePurchaseItem homePurchaseItem);

    /**
     * 全量更新采购项：类型名称与预算区间由服务端重算，图片整体替换。
     *
     * @param homePurchaseItem 采购项（含主键、预算原文与图片）
     * @return 受影响行数
     */
    int updateHomePurchaseItem(HomePurchaseItem homePurchaseItem);

    /**
     * 删除采购项（连同图片）。
     *
     * @param id 主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteHomePurchaseItem(Long id);

    /**
     * 按主键查询（含图片）。
     *
     * @param id 主键
     * @return 采购项；不存在返回 null
     */
    HomePurchaseItem getHomePurchaseItem(Long id);

    /**
     * 分页查询（含图片，图片按采购项批量取回）。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<HomePurchaseItem> findPage(HomePurchaseItemQueryParam query);

    /**
     * 列表查询（含图片）。
     *
     * @param query 查询参数
     * @return 采购项列表
     */
    List<HomePurchaseItem> findList(HomePurchaseItemQueryParam query);

    /**
     * AI 推荐 3 款候选产品（能力配置见 sys_ai_capability 的 HOME_PURCHASE / PRODUCT_RECOMMEND）。
     *
     * @param bigTypeCode 大类编码
     * @param typeCode 小类编码
     * @param budgetText 用户填写的预算原文，可为空
     * @param remark 用户补充说明，可为空
     * @return 候选产品列表；模型未按约定返回 JSON 时，原文放在单条建议的 reason 中
     */
    List<ProductSuggestionView> recommendProducts(String bigTypeCode, String typeCode, String budgetText, String remark);

    /**
     * 一句话录入：把用户口语转成采购项草稿（只做抽取，不落库，由用户确认后再走新增）。
     *
     * @param text 用户原话
     * @return 解析草稿；类型/预算非法或与录入无关时抛业务异常
     */
    PurchaseItemDraftView parseByText(String text);
}
