package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.HomePurchaseItemManager;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeGroup;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.dto.ProductSuggestionView;
import com.jakt.aiplatform.core.model.dto.PurchaseItemDraftView;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;
import com.jakt.aiplatform.core.model.param.ProductRecommendParam;
import com.jakt.aiplatform.core.service.FurnitureTypeService;
import com.jakt.aiplatform.core.service.HomePurchaseItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 家庭装修采购项管理实现类
 */
@Service
public class HomePurchaseItemManagerImpl implements HomePurchaseItemManager {

    /** 家庭装修采购项领域服务。 */
    private final HomePurchaseItemService homePurchaseItemService;

    /** 家具类型领域服务。 */
    private final FurnitureTypeService furnitureTypeService;

    public HomePurchaseItemManagerImpl(HomePurchaseItemService homePurchaseItemService,
                                       FurnitureTypeService furnitureTypeService) {
        this.homePurchaseItemService = homePurchaseItemService;
        this.furnitureTypeService = furnitureTypeService;
    }

    @Override
    public HomePurchaseItem createHomePurchaseItem(HomePurchaseItem homePurchaseItem) {
        HomePurchaseItem created = homePurchaseItemService.createHomePurchaseItem(homePurchaseItem);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建家庭装修采购项成功 id={} 名称={}",
                created.getId(), created.getProductName());
        return created;
    }

    @Override
    public HomePurchaseItem getHomePurchaseItem(Long id) {
        return homePurchaseItemService.getHomePurchaseItem(id);
    }

    @Override
    public PageResult<HomePurchaseItem> pageHomePurchaseItems(HomePurchaseItemQueryParam query) {
        return homePurchaseItemService.findPage(query);
    }

    @Override
    public List<HomePurchaseItem> listHomePurchaseItems(HomePurchaseItemQueryParam query) {
        return homePurchaseItemService.findList(query);
    }

    @Override
    public int updateHomePurchaseItem(HomePurchaseItem homePurchaseItem) {
        int affected = homePurchaseItemService.updateHomePurchaseItem(homePurchaseItem);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新家庭装修采购项成功 id={} 影响行数={}",
                homePurchaseItem.getId(), affected);
        return affected;
    }

    @Override
    public int deleteHomePurchaseItem(Long id) {
        int affected = homePurchaseItemService.deleteHomePurchaseItem(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除家庭装修采购项成功 id={} 影响行数={}", id, affected);
        return affected;
    }

    @Override
    public List<FurnitureTypeGroup> listFurnitureTypes() {
        return furnitureTypeService.listGroups();
    }

    @Override
    public List<ProductSuggestionView> recommendProducts(ProductRecommendParam param) {
        List<ProductSuggestionView> suggestions = homePurchaseItemService.recommendProducts(param);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "AI 产品推荐完成 类型={} 关键词={} 偏好={} 返回条数={}",
                param.getTypeCode(), param.getProductName(), param.getPreference(), suggestions.size());
        return suggestions;
    }

    @Override
    public PurchaseItemDraftView parseByText(String text) {
        PurchaseItemDraftView draft = homePurchaseItemService.parseByText(text);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "一句话录入解析完成 类型={} 名称={}",
                draft.getTypeName(), draft.getProductName());
        return draft;
    }
}
