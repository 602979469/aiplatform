package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.result.Result;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.util.tools.JsonUtil;
import com.jakt.aiplatform.core.model.constant.HomePurchaseConstant;
import com.jakt.aiplatform.core.model.domain.BudgetRange;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeGroup;
import com.jakt.aiplatform.core.model.domain.FurnitureTypeItem;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItemImage;
import com.jakt.aiplatform.core.model.dto.ProductSuggestionView;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemImageQueryParam;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;
import com.jakt.aiplatform.core.repository.HomePurchaseItemImageRepository;
import com.jakt.aiplatform.core.repository.HomePurchaseItemRepository;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import com.jakt.aiplatform.core.service.FurnitureTypeService;
import com.jakt.aiplatform.core.service.HomePurchaseItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 家庭装修采购项领域服务实现：预算原文解析成区间、类型快照校验、图片整体替换（多写走事务模板）。
 */
@Service
public class HomePurchaseItemServiceImpl implements HomePurchaseItemService {

    /** 家庭装修采购项仓储。 */
    private final HomePurchaseItemRepository homePurchaseItemRepository;

    /** 家庭装修采购项图片仓储。 */
    private final HomePurchaseItemImageRepository homePurchaseItemImageRepository;

    /** 家具类型领域服务。 */
    private final FurnitureTypeService furnitureTypeService;

    /** AI 能力领域服务。 */
    private final AiCapabilityService aiCapabilityService;

    /** 事务模板：采购项与图片必须同生共死。 */
    private final TransactionTemplate transactionTemplate;

    public HomePurchaseItemServiceImpl(HomePurchaseItemRepository homePurchaseItemRepository,
                                       HomePurchaseItemImageRepository homePurchaseItemImageRepository,
                                       FurnitureTypeService furnitureTypeService,
                                       AiCapabilityService aiCapabilityService,
                                       TransactionTemplate transactionTemplate) {
        this.homePurchaseItemRepository = homePurchaseItemRepository;
        this.homePurchaseItemImageRepository = homePurchaseItemImageRepository;
        this.furnitureTypeService = furnitureTypeService;
        this.aiCapabilityService = aiCapabilityService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public HomePurchaseItem createHomePurchaseItem(HomePurchaseItem homePurchaseItem) {
        prepareItem(homePurchaseItem);
        homePurchaseItem.setUserId(UserContext.getUserId());
        Result<HomePurchaseItem> result = BizTemplate.execute(transactionTemplate, () -> {
            HomePurchaseItem created = homePurchaseItemRepository.insert(homePurchaseItem);
            saveImages(created.getId(), homePurchaseItem.getImages());
            return created;
        });
        HomePurchaseItem created = unwrap(result);
        created.setImages(homePurchaseItemImageRepository.findByItemId(created.getId()));
        return created;
    }

    @Override
    public int updateHomePurchaseItem(HomePurchaseItem homePurchaseItem) {
        AssertUtil.throwErrWhenNull(homePurchaseItem, ErrorCodeEnum.PARAM_INVALID, "采购项不能为空");
        HomePurchaseItem existing = homePurchaseItemRepository.findById(homePurchaseItem.getId());
        AssertUtil.throwErrWhenNull(existing, BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                "采购项不存在或已删除：" + homePurchaseItem.getId());
        prepareItem(homePurchaseItem);
        // 全量更新会覆盖 user_id 列，录入人保持原值
        homePurchaseItem.setUserId(existing.getUserId());
        Result<Integer> result = BizTemplate.execute(transactionTemplate, () -> {
            int affected = homePurchaseItemRepository.update(homePurchaseItem);
            saveImages(homePurchaseItem.getId(), homePurchaseItem.getImages());
            return affected;
        });
        return unwrap(result);
    }

    @Override
    public int deleteHomePurchaseItem(Long id) {
        Result<Integer> result = BizTemplate.execute(transactionTemplate, () -> {
            int affected = homePurchaseItemRepository.deleteById(id);
            homePurchaseItemImageRepository.deleteByItemId(id);
            return affected;
        });
        return unwrap(result);
    }

    @Override
    public HomePurchaseItem getHomePurchaseItem(Long id) {
        HomePurchaseItem homePurchaseItem = homePurchaseItemRepository.findById(id);
        if (ObjectUtil.isNull(homePurchaseItem)) {
            return null;
        }
        homePurchaseItem.setImages(homePurchaseItemImageRepository.findByItemId(id));
        return homePurchaseItem;
    }

    @Override
    public PageResult<HomePurchaseItem> findPage(HomePurchaseItemQueryParam query) {
        PageResult<HomePurchaseItem> page = homePurchaseItemRepository.findPage(query);
        attachImages(page.getDataList());
        return page;
    }

    @Override
    public List<HomePurchaseItem> findList(HomePurchaseItemQueryParam query) {
        List<HomePurchaseItem> list = homePurchaseItemRepository.findList(query);
        attachImages(list);
        return list;
    }

    @Override
    public List<ProductSuggestionView> recommendProducts(String bigTypeCode, String typeCode,
                                                         String budgetText, String remark) {
        FurnitureTypeItem type = furnitureTypeService.findType(bigTypeCode, typeCode);
        AssertUtil.throwErrWhenNull(type, BizErrorCodeEnum.PURCHASE_TYPE_NOT_MATCHED,
                "家具类型不在配置范围内：" + typeCode);
        String input = StrUtil.format("家具类型：{}\n预算区间：{}\n补充说明：{}",
                type.getName(),
                StrUtil.blankToDefault(budgetText, "未提供"),
                StrUtil.blankToDefault(remark, "无"));
        String reply = aiCapabilityService.invoke(HomePurchaseConstant.SCENE_CODE,
                HomePurchaseConstant.CAPABILITY_PRODUCT_RECOMMEND, input);
        return parseSuggestions(reply);
    }

    /**
     * 入库前的统一处理：类型快照、预算区间、数量默认值、图片规整。
     */
    private void prepareItem(HomePurchaseItem homePurchaseItem) {
        AssertUtil.throwErrWhenNull(homePurchaseItem, ErrorCodeEnum.PARAM_INVALID, "采购项不能为空");
        fillTypeSnapshot(homePurchaseItem);
        fillBudget(homePurchaseItem);
        fillQuantity(homePurchaseItem);
        normalizeImages(homePurchaseItem);
    }

    /**
     * 类型必须在后端配置内，并把名称快照写入采购项（配置改名不影响历史数据）。
     */
    private void fillTypeSnapshot(HomePurchaseItem homePurchaseItem) {
        FurnitureTypeGroup group = furnitureTypeService.findGroup(homePurchaseItem.getBigTypeCode());
        AssertUtil.throwErrWhenNull(group, BizErrorCodeEnum.PURCHASE_TYPE_NOT_MATCHED,
                "家具大类不在配置范围内：" + homePurchaseItem.getBigTypeCode());
        FurnitureTypeItem type = furnitureTypeService.findType(
                homePurchaseItem.getBigTypeCode(), homePurchaseItem.getTypeCode());
        AssertUtil.throwErrWhenNull(type, BizErrorCodeEnum.PURCHASE_TYPE_NOT_MATCHED,
                "家具类型不在配置范围内：" + homePurchaseItem.getTypeCode());
        homePurchaseItem.setBigTypeName(group.getName());
        homePurchaseItem.setTypeName(type.getName());
    }

    /**
     * 预算原文入库，同时解析出单件上下限供报表统计。
     */
    private void fillBudget(HomePurchaseItem homePurchaseItem) {
        BudgetRange budgetRange = BudgetRange.parse(homePurchaseItem.getBudgetText());
        homePurchaseItem.setBudgetMin(budgetRange.getMin());
        homePurchaseItem.setBudgetMax(budgetRange.getMax());
    }

    /**
     * 数量缺省为 1。
     */
    private void fillQuantity(HomePurchaseItem homePurchaseItem) {
        if (ObjectUtil.isNull(homePurchaseItem.getQuantity()) || homePurchaseItem.getQuantity() < 1) {
            homePurchaseItem.setQuantity(HomePurchaseConstant.DEFAULT_QUANTITY);
        }
    }

    /**
     * 图片去重（按 fileId，保留先后顺序）并校验数量上限。
     */
    private void normalizeImages(HomePurchaseItem homePurchaseItem) {
        List<HomePurchaseItemImage> images = homePurchaseItem.getImages();
        if (CollUtil.isEmpty(images)) {
            homePurchaseItem.setImages(new ArrayList<>());
            return;
        }
        Map<Long, HomePurchaseItemImage> distinctImages = new LinkedHashMap<>();
        for (HomePurchaseItemImage image : images) {
            AssertUtil.throwErrWhenNull(image.getFileId(), ErrorCodeEnum.PARAM_INVALID, "图片文件ID不能为空");
            distinctImages.putIfAbsent(image.getFileId(), image);
        }
        AssertUtil.throwErrWhenTrue(distinctImages.size() > HomePurchaseConstant.MAX_IMAGE_COUNT,
                BizErrorCodeEnum.PURCHASE_IMAGE_LIMIT_EXCEEDED,
                "单个采购项最多 " + HomePurchaseConstant.MAX_IMAGE_COUNT + " 张图片");
        homePurchaseItem.setImages(new ArrayList<>(distinctImages.values()));
    }

    /**
     * 图片整体替换：先清空该采购项图片，再按入参顺序重建。
     */
    private void saveImages(Long itemId, List<HomePurchaseItemImage> images) {
        homePurchaseItemImageRepository.deleteByItemId(itemId);
        if (CollUtil.isEmpty(images)) {
            return;
        }
        int orderNum = 0;
        for (HomePurchaseItemImage image : images) {
            HomePurchaseItemImage row = new HomePurchaseItemImage();
            row.setItemId(itemId);
            row.setFileId(image.getFileId());
            row.setOrderNum(orderNum++);
            homePurchaseItemImageRepository.insert(row);
        }
    }

    /**
     * 列表页图片批量回填，避免逐行查询。
     */
    private void attachImages(List<HomePurchaseItem> items) {
        if (CollUtil.isEmpty(items)) {
            return;
        }
        HomePurchaseItemImageQueryParam query = new HomePurchaseItemImageQueryParam();
        query.setItemIds(ConvertUtil.map(items, HomePurchaseItem::getId));
        List<HomePurchaseItemImage> images = homePurchaseItemImageRepository.findList(query);
        Map<Long, List<HomePurchaseItemImage>> imageMap = images.stream()
                .collect(Collectors.groupingBy(HomePurchaseItemImage::getItemId));
        for (HomePurchaseItem item : items) {
            item.setImages(imageMap.getOrDefault(item.getId(), List.of()));
        }
    }

    /**
     * 解析模型返回的 JSON 数组；解析不出来时降级为原文展示，避免用户看到报错。
     */
    private List<ProductSuggestionView> parseSuggestions(String reply) {
        List<ProductSuggestionView> suggestions = parseJsonArray(reply);
        if (CollUtil.isNotEmpty(suggestions)) {
            return suggestions;
        }
        LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "AI 推荐未返回 JSON 数组，降级为原文展示");
        ProductSuggestionView fallback = new ProductSuggestionView();
        fallback.setReason(reply);
        return List.of(fallback);
    }

    /**
     * 提取回复中的 JSON 数组（允许模型包 ```json 代码块或加前后说明）。
     *
     * <p>必须按「第一个 [ 到最后一个 ]」截取：元素内部可能还有嵌套数组（如 highlights），
     * 用第一个 ] 会截断成非法 JSON。
     */
    private List<ProductSuggestionView> parseJsonArray(String reply) {
        if (StrUtil.isBlank(reply)) {
            return List.of();
        }
        int start = reply.indexOf('[');
        int end = reply.lastIndexOf(']');
        if (start < 0 || end <= start) {
            return List.of();
        }
        String json = reply.substring(start, end + 1);
        try {
            return JsonUtil.parseArray(json, ProductSuggestionView.class);
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "AI 推荐 JSON 解析失败：{}", e.getMessage());
            return List.of();
        }
    }

    /**
     * 事务结果解包：失败按业务异常抛出，交由 web 层 ApiTemplate 统一处理。
     */
    private <T> T unwrap(Result<T> result) {
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
        return result.getData();
    }
}
