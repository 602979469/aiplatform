package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.jakt.aiplatform.biz.service.HomeMessageManager;
import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ClientInfoUtil;
import com.jakt.aiplatform.core.model.domain.HomeMessage;
import com.jakt.aiplatform.web.assembler.HomeMessageAssembler;
import com.jakt.aiplatform.web.param.HomeMessageCreateRequest;
import com.jakt.aiplatform.web.param.HomeMessageQueryRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.HomeMessageResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页留言板接口：匿名留言，身份按客户端 IP 归属（同一 IP 同色同头像）。
 *
 * <p>Controller 只做参数校验、DTO 转换与结果包装，不含业务规则。
 * 留言板允许未登录访客查看与发言（面试官不需要账号），因此接口标注 {@link SaIgnore}。
 */
@RestController
@RequestMapping("/api/message")
@Tag(name = "首页留言板")
public class HomeMessageController {

    /** 首页留言板 Manager。 */
    private final HomeMessageManager homeMessageManager;

    public HomeMessageController(HomeMessageManager homeMessageManager) {
        this.homeMessageManager = homeMessageManager;
    }

    /**
     * 发布留言。
     *
     * @param request 发布请求（留言内容）
     * @return 新增后的留言（含匿名身份）
     */
    @PostMapping
    @SaIgnore
    public ApiResult<HomeMessageResponse> create(@RequestBody(required = false) HomeMessageCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<HomeMessageCreateRequest, HomeMessageResponse>() {
            @Override
            public void beforeService(HomeMessageCreateRequest param) {
                AssertUtil.throwErrWhenNull(param, ErrorCodeEnum.PARAM_INVALID, "请求体不能为空");
            }

            @Override
            public HomeMessageResponse execute(HomeMessageCreateRequest param) {
                String clientIp = ClientInfoUtil.getClientIp();
                HomeMessage created = homeMessageManager.postMessage(param.getContent(), clientIp, UserContext.getUserName());
                return HomeMessageAssembler.toResponse(created, clientIp);
            }
        });
    }

    /**
     * 分页查询留言（最新在前，默认每页 10 条）。
     *
     * @param request 分页请求
     * @return 分页结果（含「我的留言」标记）
     */
    @GetMapping("/page")
    @SaIgnore
    public ApiResult<PageResult<HomeMessageResponse>> page(HomeMessageQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<HomeMessageQueryRequest,
                PageResult<HomeMessageResponse>>() {
            @Override
            public PageResult<HomeMessageResponse> execute(HomeMessageQueryRequest param) {
                String clientIp = ClientInfoUtil.getClientIp();
                PageResult<HomeMessage> page = homeMessageManager.pageMessages(param.getPageNum(), param.getPageSize());
                PageResult<HomeMessageResponse> result = new PageResult<>();
                result.setTotal(page.getTotal());
                result.setPageNum(page.getPageNum());
                result.setPageSize(page.getPageSize());
                result.setDataList(HomeMessageAssembler.toResponses(page.getDataList(), clientIp));
                return result;
            }
        });
    }
}
