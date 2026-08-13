package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysNoticeManager;
import com.jakt.aiplatform.web.assembler.SysNoticeAssembler;
import com.jakt.aiplatform.web.checker.SysNoticeParamChecker;
import com.jakt.aiplatform.web.param.SysNoticeCreateRequest;
import com.jakt.aiplatform.web.param.SysNoticeQueryRequest;
import com.jakt.aiplatform.web.param.SysNoticeUpdateRequest;
import com.jakt.aiplatform.web.result.SysNoticeResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.result.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知公告管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysNotices")
@Tag(name = "通知公告管理")
public class SysNoticeController {

    /** 通知公告 Manager。 */
    private final SysNoticeManager sysNoticeManager;

    public SysNoticeController(SysNoticeManager sysNoticeManager) {
        this.sysNoticeManager = sysNoticeManager;
    }

    /**
     * 创建通知公告。
     *
     * @param request 创建通知公告请求体
     * @return 创建成功后的通知公告信息
     */
    @PostMapping
    public AiPlatformResult<SysNoticeResponse> create(@RequestBody SysNoticeCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysNoticeCreateRequest param) {
                SysNoticeParamChecker.checkSysNoticeCreateRequest(param);
            }

            @Override
            public SysNoticeResponse execute(SysNoticeCreateRequest param) {
                // TODO 接入 SysNoticeManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(SysNoticeCreateRequest param, SysNoticeResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询通知公告。
     *
     * @param id 通知公告 ID
     * @return 通知公告信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysNoticeResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysNoticeParamChecker.checkId(param);
            }

            @Override
            public SysNoticeResponse execute(Long param) {
                // TODO 接入 SysNoticeManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(Long param, SysNoticeResponse result) {
            }
        });
    }

    /**
     * 分页查询通知公告。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysNoticeResponse>> page(SysNoticeQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysNoticeQueryRequest param) {
                SysNoticeParamChecker.checkSysNoticeQueryRequest(param);
            }

            @Override
            public PageResult<SysNoticeResponse> execute(SysNoticeQueryRequest param) {
                // TODO 接入 SysNoticeManager（RuoYi 移植过渡）
                return null;
            }
        });
    }

    /**
     * 更新通知公告（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      通知公告 ID
     * @param request 更新内容
     * @return 更新后的通知公告信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysNoticeUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysNoticeUpdateRequest param) {
                SysNoticeParamChecker.checkId(id);
                SysNoticeParamChecker.checkSysNoticeUpdateRequest(param);
            }

            @Override
            public void execute(SysNoticeUpdateRequest param) {
                // TODO 接入 SysNoticeManager（RuoYi 移植过渡）
            }
        });
    }

    /**
     * 删除通知公告。
     *
     * @param id 通知公告 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysNoticeParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                // TODO 接入 SysNoticeManager（RuoYi 移植过渡）
            }
        });
    }
}
