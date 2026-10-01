package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeGroup;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeItem;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItemImage;
import com.jakt.aiplatform.core.model.dto.ProductSuggestionView;
import com.jakt.aiplatform.core.model.dto.PurchaseItemDraftView;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;
import com.jakt.aiplatform.web.param.HomePurchaseItemCreateRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemQueryRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemUpdateRequest;
import com.jakt.aiplatform.web.result.FurnitureTypeResponse;
import com.jakt.aiplatform.web.result.HomePurchaseItemImageResponse;
import com.jakt.aiplatform.web.result.HomePurchaseItemResponse;
import com.jakt.aiplatform.web.result.ProductSuggestionResponse;
import com.jakt.aiplatform.web.result.PurchaseItemDraftResponse;

import java.util.List;

/**
 * 家庭装修采购项对象组装器：DTO 与领域模型互转，只存在于 web。
 *
 * <p>大类/小类名称、预算区间都由服务端根据编码与预算原文重算，前端只提交编码与原文。
 */
public final class HomePurchaseItemAssembler {

    private HomePurchaseItemAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建请求 DTO；为空返回 null
     * @return 采购项领域模型
     */
    public static HomePurchaseItem toModel(HomePurchaseItemCreateRequest request) {
        if (request == null) {
            return null;
        }
        HomePurchaseItem homePurchaseItem = new HomePurchaseItem();
        homePurchaseItem.setBigTypeCode(request.getBigTypeCode());
        homePurchaseItem.setTypeCode(request.getTypeCode());
        homePurchaseItem.setProductName(request.getProductName());
        homePurchaseItem.setQuantity(request.getQuantity());
        homePurchaseItem.setBudgetText(request.getBudgetText());
        homePurchaseItem.setInstallFee(request.getInstallFee());
        homePurchaseItem.setRemark(request.getRemark());
        homePurchaseItem.setImages(toImageModels(request.getFileIds()));
        return homePurchaseItem;
    }

    /**
     * 更新请求 DTO + 路径主键 → 领域模型。
     *
     * @param request 更新请求 DTO；为空返回 null
     * @param id 路径中的主键
     * @return 采购项领域模型
     */
    public static HomePurchaseItem toModel(HomePurchaseItemUpdateRequest request, Long id) {
        if (request == null) {
            return null;
        }
        HomePurchaseItem homePurchaseItem = new HomePurchaseItem();
        homePurchaseItem.setId(id);
        homePurchaseItem.setBigTypeCode(request.getBigTypeCode());
        homePurchaseItem.setTypeCode(request.getTypeCode());
        homePurchaseItem.setProductName(request.getProductName());
        homePurchaseItem.setQuantity(request.getQuantity());
        homePurchaseItem.setBudgetText(request.getBudgetText());
        homePurchaseItem.setInstallFee(request.getInstallFee());
        homePurchaseItem.setRemark(request.getRemark());
        homePurchaseItem.setImages(toImageModels(request.getFileIds()));
        return homePurchaseItem;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 查询参数
     */
    public static HomePurchaseItemQueryParam toQueryParam(HomePurchaseItemQueryRequest request) {
        if (request == null) {
            return new HomePurchaseItemQueryParam();
        }
        HomePurchaseItemQueryParam param = new HomePurchaseItemQueryParam();
        param.setId(request.getId());
        param.setBigTypeCode(request.getBigTypeCode());
        param.setTypeCode(request.getTypeCode());
        param.setProductName(request.getProductName());
        param.setUserId(request.getUserId());
        param.setCreateTimeBegin(request.getCreateTimeBegin());
        param.setCreateTimeEnd(request.getCreateTimeEnd());
        param.setUpdateTimeBegin(request.getUpdateTimeBegin());
        param.setUpdateTimeEnd(request.getUpdateTimeEnd());
        param.setPageNum(ObjectUtil.defaultIfNull(request.getPageNum(), PageConstants.DEFAULT_PAGE_NUM));
        param.setPageSize(ObjectUtil.defaultIfNull(request.getPageSize(), PageConstants.DEFAULT_PAGE_SIZE));
        return param;
    }

    /**
     * 领域模型 → 响应 VO。
     *
     * @param homePurchaseItem 采购项领域模型；为空返回 null
     * @return 响应 VO
     */
    public static HomePurchaseItemResponse toResponse(HomePurchaseItem homePurchaseItem) {
        if (homePurchaseItem == null) {
            return null;
        }
        HomePurchaseItemResponse response = new HomePurchaseItemResponse();
        response.setId(homePurchaseItem.getId());
        response.setBigTypeCode(homePurchaseItem.getBigTypeCode());
        response.setBigTypeName(homePurchaseItem.getBigTypeName());
        response.setTypeCode(homePurchaseItem.getTypeCode());
        response.setTypeName(homePurchaseItem.getTypeName());
        response.setProductName(homePurchaseItem.getProductName());
        response.setQuantity(homePurchaseItem.getQuantity());
        response.setBudgetText(homePurchaseItem.getBudgetText());
        response.setBudgetMin(homePurchaseItem.getBudgetMin());
        response.setBudgetMax(homePurchaseItem.getBudgetMax());
        response.setInstallFee(homePurchaseItem.getInstallFee());
        response.setRemark(homePurchaseItem.getRemark());
        response.setUserId(homePurchaseItem.getUserId());
        response.setImages(toImageResponses(homePurchaseItem.getImages()));
        response.setCreateTime(homePurchaseItem.getCreateTime());
        response.setUpdateTime(homePurchaseItem.getUpdateTime());
        return response;
    }

    /**
     * 家具类型树 → 响应 VO。
     *
     * @param groups 类型树；为空返回空列表
     * @return 类型树响应
     */
    public static List<FurnitureTypeResponse> toTypeResponses(List<FurnitureTypeGroup> groups) {
        if (CollUtil.isEmpty(groups)) {
            return List.of();
        }
        return groups.stream().map(HomePurchaseItemAssembler::toTypeResponse).toList();
    }

    /**
     * AI 推荐结果 → 响应 VO。
     *
     * @param suggestions 推荐结果；为空返回空列表
     * @return 推荐结果响应
     */
    public static List<ProductSuggestionResponse> toSuggestionResponses(List<ProductSuggestionView> suggestions) {
        if (CollUtil.isEmpty(suggestions)) {
            return List.of();
        }
        return suggestions.stream().map(suggestion -> {
            ProductSuggestionResponse response = new ProductSuggestionResponse();
            response.setName(suggestion.getName());
            response.setPriceRange(suggestion.getPriceRange());
            response.setReason(suggestion.getReason());
            response.setHighlights(suggestion.getHighlights());
            return response;
        }).toList();
    }

    /**
     * 一句话录入草稿 → 响应 VO（给用户确认的预期效果）。
     *
     * @param draft 解析草稿；为空返回 null
     * @return 草稿响应
     */
    public static PurchaseItemDraftResponse toDraftResponse(PurchaseItemDraftView draft) {
        if (draft == null) {
            return null;
        }
        PurchaseItemDraftResponse response = new PurchaseItemDraftResponse();
        response.setValid(draft.getValid());
        response.setBigTypeCode(draft.getBigTypeCode());
        response.setBigTypeName(draft.getBigTypeName());
        response.setTypeCode(draft.getTypeCode());
        response.setTypeName(draft.getTypeName());
        response.setProductName(draft.getProductName());
        response.setBudgetText(draft.getBudgetText());
        response.setQuantity(draft.getQuantity());
        return response;
    }

    /**
     * 文件ID列表 → 图片领域模型（顺序即展示顺序，itemId 由服务端落库时补）。
     */
    private static List<HomePurchaseItemImage> toImageModels(List<Long> fileIds) {
        if (CollUtil.isEmpty(fileIds)) {
            return List.of();
        }
        return fileIds.stream().map(fileId -> {
            HomePurchaseItemImage image = new HomePurchaseItemImage();
            image.setFileId(fileId);
            return image;
        }).toList();
    }

    /**
     * 图片领域模型 → 响应 VO。
     */
    private static List<HomePurchaseItemImageResponse> toImageResponses(List<HomePurchaseItemImage> images) {
        if (CollUtil.isEmpty(images)) {
            return List.of();
        }
        return images.stream().map(image -> {
            HomePurchaseItemImageResponse response = new HomePurchaseItemImageResponse();
            response.setId(image.getId());
            response.setFileId(image.getFileId());
            response.setOrderNum(image.getOrderNum());
            return response;
        }).toList();
    }

    /**
     * 大类 → 响应 VO（含小类）。
     */
    private static FurnitureTypeResponse toTypeResponse(FurnitureTypeGroup group) {
        FurnitureTypeResponse response = new FurnitureTypeResponse();
        response.setCode(group.getCode());
        response.setName(group.getName());
        response.setIcon(group.getIcon());
        if (CollUtil.isNotEmpty(group.getChildren())) {
            response.setChildren(group.getChildren().stream()
                    .map(HomePurchaseItemAssembler::toTypeResponse).toList());
        } else {
            response.setChildren(List.of());
        }
        return response;
    }

    /**
     * 小类 → 响应 VO。
     */
    private static FurnitureTypeResponse toTypeResponse(FurnitureTypeItem item) {
        FurnitureTypeResponse response = new FurnitureTypeResponse();
        response.setCode(item.getCode());
        response.setName(item.getName());
        response.setIcon(item.getIcon());
        response.setChildren(List.of());
        return response;
    }
}
