package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.HomePurchaseItemManager;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.dto.ProductSuggestionView;
import com.jakt.aiplatform.core.model.dto.PurchaseItemDraftView;
import com.jakt.aiplatform.web.assembler.HomePurchaseItemAssembler;
import com.jakt.aiplatform.web.checker.HomePurchaseItemParamChecker;
import com.jakt.aiplatform.web.param.HomePurchaseItemCreateRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemQueryRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemRecommendRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemParseRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemUpdateRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.FurnitureTypeResponse;
import com.jakt.aiplatform.web.result.HomePurchaseItemResponse;
import com.jakt.aiplatform.web.result.ProductSuggestionResponse;
import com.jakt.aiplatform.web.result.PurchaseItemDraftResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 家庭装修采购项管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 ApiTemplate。
 */
@RestController
@RequestMapping("/api/v1/homePurchaseItems")
@Tag(name = "家庭装修采购项管理")
public class HomePurchaseItemController {

    /** 家庭装修采购项 Manager。 */
    private final HomePurchaseItemManager homePurchaseItemManager;

    public HomePurchaseItemController(HomePurchaseItemManager homePurchaseItemManager) {
        this.homePurchaseItemManager = homePurchaseItemManager;
    }

    /**
     * 家具类型树（大类 → 小类，含简易图标）：移动端/电脑端下拉框数据源。
     *
     * @return 类型树
     */
    @GetMapping("/types")
    public ApiResult<List<FurnitureTypeResponse>> types() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<FurnitureTypeResponse>>() {

            @Override
            public List<FurnitureTypeResponse> execute(Object param) {
                return HomePurchaseItemAssembler.toTypeResponses(homePurchaseItemManager.listFurnitureTypes());
            }
        });
    }

    /**
     * 创建家庭装修采购项。
     *
     * @param request 创建请求体
     * @return 创建后的采购项
     */
    @PostMapping
    public ApiResult<HomePurchaseItemResponse> create(@RequestBody HomePurchaseItemCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(HomePurchaseItemCreateRequest param) {
                HomePurchaseItemParamChecker.checkHomePurchaseItemCreateRequest(param);
            }

            @Override
            public HomePurchaseItemResponse execute(HomePurchaseItemCreateRequest param) {
                HomePurchaseItem homePurchaseItem =
                        homePurchaseItemManager.createHomePurchaseItem(HomePurchaseItemAssembler.toModel(param));
                return HomePurchaseItemAssembler.toResponse(homePurchaseItem);
            }
        });
    }

    /**
     * 按主键查询家庭装修采购项（含参考图片）。
     *
     * @param id 主键
     * @return 采购项信息
     */
    @GetMapping("/{id}")
    public ApiResult<HomePurchaseItemResponse> get(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                HomePurchaseItemParamChecker.checkId(id);
            }

            @Override
            public HomePurchaseItemResponse execute(Long param) {
                HomePurchaseItem homePurchaseItem = homePurchaseItemManager.getHomePurchaseItem(id);
                AssertUtil.throwErrWhenNull(homePurchaseItem, ErrorCodeEnum.PARAM_INVALID, "家庭装修采购项不存在");
                return HomePurchaseItemAssembler.toResponse(homePurchaseItem);
            }
        });
    }

    /**
     * 分页查询家庭装修采购项（含参考图片）。
     *
     * @param request 查询条件（含分页参数）
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResult<PageResult<HomePurchaseItemResponse>> page(HomePurchaseItemQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(HomePurchaseItemQueryRequest param) {
                HomePurchaseItemParamChecker.checkHomePurchaseItemQueryRequest(param);
            }

            @Override
            public PageResult<HomePurchaseItemResponse> execute(HomePurchaseItemQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new HomePurchaseItemQueryRequest());
                PageResult<HomePurchaseItem> page = homePurchaseItemManager.pageHomePurchaseItems(
                        HomePurchaseItemAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, HomePurchaseItemAssembler::toResponse);
            }
        });
    }

    /**
     * 列表查询（不分页）：预算报告页一次拉取全部采购项。
     *
     * @param request 查询条件
     * @return 采购项列表
     */
    @GetMapping("/list")
    public ApiResult<List<HomePurchaseItemResponse>> list(HomePurchaseItemQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(HomePurchaseItemQueryRequest param) {
                HomePurchaseItemParamChecker.checkHomePurchaseItemQueryRequest(param);
            }

            @Override
            public List<HomePurchaseItemResponse> execute(HomePurchaseItemQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new HomePurchaseItemQueryRequest());
                List<HomePurchaseItem> items = homePurchaseItemManager.listHomePurchaseItems(
                        HomePurchaseItemAssembler.toQueryParam(param));
                return ConvertUtil.map(items, HomePurchaseItemAssembler::toResponse);
            }
        });
    }

    /**
     * AI 推荐 3 款候选产品：给某个家具类型（如空调）推荐经济性好、销量高、口碑好的产品。
     *
     * @param request 推荐请求体
     * @return 候选产品列表
     */
    @PostMapping("/recommend")
    public ApiResult<List<ProductSuggestionResponse>> recommend(@RequestBody HomePurchaseItemRecommendRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(HomePurchaseItemRecommendRequest param) {
                HomePurchaseItemParamChecker.checkHomePurchaseItemRecommendRequest(param);
            }

            @Override
            public List<ProductSuggestionResponse> execute(HomePurchaseItemRecommendRequest param) {
                List<ProductSuggestionView> suggestions = homePurchaseItemManager.recommendProducts(
                        param.getBigTypeCode(), param.getTypeCode(), param.getBudgetText(),
                        param.getRemark(), param.getPreference());
                return HomePurchaseItemAssembler.toSuggestionResponses(suggestions);
            }
        });
    }

    /**
     * 一句话录入：把用户口语转成采购项草稿（移动端专用，只做抽取不落库）。
     *
     * <p>返回给用户确认；确认后走 {@code POST /api/v1/homePurchaseItems} 落库，校验逻辑与手动录入一致。
     *
     * @param request 一句话请求体
     * @return 解析草稿（给用户看的预期效果）
     */
    @PostMapping("/parse")
    public ApiResult<PurchaseItemDraftResponse> parse(@RequestBody HomePurchaseItemParseRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(HomePurchaseItemParseRequest param) {
                HomePurchaseItemParamChecker.checkHomePurchaseItemParseRequest(param);
            }

            @Override
            public PurchaseItemDraftResponse execute(HomePurchaseItemParseRequest param) {
                PurchaseItemDraftView draft = homePurchaseItemManager.parseByText(param.getText());
                return HomePurchaseItemAssembler.toDraftResponse(draft);
            }
        });
    }

    /**
     * 更新家庭装修采购项（全量）：类型名称与预算区间由服务端重算，图片整体替换。
     *
     * <p>一句话录入解析出的草稿，最终也是走这个接口落库，校验逻辑完全一致。
     *
     * @param id 主键
     * @param request 更新内容
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody HomePurchaseItemUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(HomePurchaseItemUpdateRequest param) {
                HomePurchaseItemParamChecker.checkId(id);
                HomePurchaseItemParamChecker.checkHomePurchaseItemUpdateRequest(param);
            }

            @Override
            public void execute(HomePurchaseItemUpdateRequest param) {
                homePurchaseItemManager.updateHomePurchaseItem(HomePurchaseItemAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除家庭装修采购项（连同参考图片）。
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long id) {
                HomePurchaseItemParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                homePurchaseItemManager.deleteHomePurchaseItem(id);
            }
        });
    }
}
