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
import com.jakt.aiplatform.core.model.dto.PurchaseItemDraftView;
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
                                                         String budgetText, String remark, String preference) {
        FurnitureTypeItem type = furnitureTypeService.findType(bigTypeCode, typeCode);
        AssertUtil.throwErrWhenNull(type, BizErrorCodeEnum.PURCHASE_TYPE_NOT_MATCHED,
                "家具类型不在配置范围内：" + typeCode);
        String input = StrUtil.format("家具类型：{}\n预算区间：{}\n补充说明：{}\n本次偏好：{}",
                type.getName(),
                StrUtil.blankToDefault(budgetText, "未提供"),
                StrUtil.blankToDefault(remark, "无"),
                StrUtil.blankToDefault(preference, "无（按经济型/销量/口碑通用推荐）"));
        String reply = aiCapabilityService.invoke(HomePurchaseConstant.SCENE_CODE,
                HomePurchaseConstant.CAPABILITY_PRODUCT_RECOMMEND, input);
        return parseSuggestions(reply);
    }

    @Override
    public PurchaseItemDraftView parseByText(String text) {
        AssertUtil.throwErrWhenBlank(text, ErrorCodeEnum.PARAM_INVALID, "说一句要添加什么吧");
        PurchaseItemDraftView draft = parseDraft(aiCapabilityService.invoke(
                HomePurchaseConstant.SCENE_CODE, HomePurchaseConstant.CAPABILITY_ITEM_PARSE, buildParseInput(text)));
        // 模型判定"这句话跟新增采购项无关" → 把原因原样抛给用户
        AssertUtil.throwErrWhenTrue(Boolean.FALSE.equals(draft.getValid()),
                BizErrorCodeEnum.PURCHASE_TYPE_NOT_MATCHED,
                StrUtil.blankToDefault(draft.getReason(), "没听懂要添加什么，换个说法试试"));

        FurnitureTypeGroup group = furnitureTypeService.findGroup(draft.getBigTypeCode());
        FurnitureTypeItem type = furnitureTypeService.findType(draft.getBigTypeCode(), draft.getTypeCode());
        // 模型偶尔只回名称不回编码：按原话兜底匹配一次
        if (ObjectUtil.isNull(type)) {
            FurnitureTypeItem matched = matchTypeByText(text);
            if (ObjectUtil.isNotNull(matched)) {
                type = matched;
                group = findGroupByTypeCode(matched.getCode());
            }
        }
        AssertUtil.throwErrWhenNull(group, BizErrorCodeEnum.PURCHASE_TYPE_NOT_MATCHED,
                "这句话里没找到对应的采购类型，换个说法试试");
        AssertUtil.throwErrWhenNull(type, BizErrorCodeEnum.PURCHASE_TYPE_NOT_MATCHED,
                "这句话里没找到对应的采购类型，换个说法试试");
        AssertUtil.throwErrWhenBlank(draft.getProductName(), ErrorCodeEnum.PARAM_INVALID, "没听清要添加什么，再说一遍？");

        // 与手动录入同一套校验：预算能解析、数量至少 1
        if (StrUtil.isBlank(draft.getBudgetText())) {
            draft.setBudgetText(StrUtil.EMPTY);
        } else {
            draft.setBudgetText(StrUtil.trim(draft.getBudgetText()));
            BudgetRange.parse(draft.getBudgetText());
        }
        if (ObjectUtil.isNull(draft.getQuantity()) || draft.getQuantity() < 1) {
            draft.setQuantity(HomePurchaseConstant.DEFAULT_QUANTITY);
        }
        draft.setBigTypeCode(group.getCode());
        draft.setBigTypeName(group.getName());
        draft.setTypeCode(type.getCode());
        draft.setTypeName(type.getName());
        draft.setProductName(StrUtil.trim(draft.getProductName()));
        draft.setValid(true);
        return draft;
    }

    /**
     * 把当前类型配置喂给模型：模型只负责"对号入座"，类型编码不允许自由发挥。
     */
    private String buildParseInput(String text) {
        StringBuilder catalog = new StringBuilder();
        for (FurnitureTypeGroup group : furnitureTypeService.listGroups()) {
            if (CollUtil.isEmpty(group.getChildren())) {
                continue;
            }
            for (FurnitureTypeItem type : group.getChildren()) {
                catalog.append(group.getCode()).append('|').append(group.getName()).append('|')
                        .append(type.getCode()).append('|').append(type.getName()).append('\n');
            }
        }
        return "可选类型清单：\n" + catalog + "\n用户原话：" + text;
    }

    /**
     * 按小类名称在原话里兜底匹配；匹配不到返回 null。
     */
    private FurnitureTypeItem matchTypeByText(String text) {
        for (FurnitureTypeGroup group : furnitureTypeService.listGroups()) {
            for (FurnitureTypeItem type : CollUtil.emptyIfNull(group.getChildren())) {
                if (StrUtil.contains(text, type.getName())) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * 按小类编码反查所属大类。
     */
    private FurnitureTypeGroup findGroupByTypeCode(String typeCode) {
        for (FurnitureTypeGroup group : furnitureTypeService.listGroups()) {
            if (CollUtil.emptyIfNull(group.getChildren()).stream()
                    .anyMatch(type -> StrUtil.equals(type.getCode(), typeCode))) {
                return group;
            }
        }
        return null;
    }

    /**
     * 解析模型返回的 JSON 对象；解析不出来时按"没听懂"处理。
     */
    private PurchaseItemDraftView parseDraft(String reply) {
        PurchaseItemDraftView draft = new PurchaseItemDraftView();
        if (StrUtil.isBlank(reply)) {
            return invalidDraft();
        }
        int start = reply.indexOf('{');
        int end = reply.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return invalidDraft();
        }
        try {
            draft = JsonUtil.parseObject(reply.substring(start, end + 1), PurchaseItemDraftView.class);
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "一句话录入 JSON 解析失败：{}", e.getMessage());
        }
        return ObjectUtil.isNull(draft) ? invalidDraft() : draft;
    }

    private PurchaseItemDraftView invalidDraft() {
        PurchaseItemDraftView draft = new PurchaseItemDraftView();
        draft.setValid(Boolean.FALSE);
        draft.setReason("没听清，再说一遍？");
        return draft;
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
     *
     * <p>预算允许先留空（先录入大类型/类型/产品名称，手机端再补）：此时区间置空，
     * 原文按空串入库（列 NOT NULL），报表侧按「待定」处理、不计入合计。
     */
    private void fillBudget(HomePurchaseItem homePurchaseItem) {
        if (StrUtil.isBlank(homePurchaseItem.getBudgetText())) {
            homePurchaseItem.setBudgetText(StrUtil.EMPTY);
            homePurchaseItem.setBudgetMin(null);
            homePurchaseItem.setBudgetMax(null);
            return;
        }
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
