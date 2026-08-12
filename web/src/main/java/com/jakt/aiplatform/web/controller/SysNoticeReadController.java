package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysNoticeReadManager;
import com.jakt.aiplatform.web.assembler.SysNoticeReadAssembler;
import com.jakt.aiplatform.web.checker.SysNoticeReadParamChecker;
import com.jakt.aiplatform.web.param.SysNoticeReadCreateRequest;
import com.jakt.aiplatform.web.param.SysNoticeReadQueryRequest;
import com.jakt.aiplatform.web.param.SysNoticeReadUpdateRequest;
import com.jakt.aiplatform.web.result.SysNoticeReadResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
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
 * 公告已读记录管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysNoticeReads")
@Tag(name = "公告已读记录管理")
public class SysNoticeReadController {

    /** 公告已读记录 Manager。 */
    private final SysNoticeReadManager sysNoticeReadManager;

    public SysNoticeReadController(SysNoticeReadManager sysNoticeReadManager) {
        this.sysNoticeReadManager = sysNoticeReadManager;
    }

    /**
     * 创建公告已读记录。
     *
     * @param request 创建公告已读记录请求体
     * @return 创建成功后的公告已读记录信息
     */
    @PostMapping
    public AiPlatformResult<SysNoticeReadResponse> create(@RequestBody SysNoticeReadCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysNoticeReadCreateRequest param) {
                SysNoticeReadParamChecker.checkSysNoticeReadCreateRequest(param);
            }

            @Override
            public SysNoticeReadResponse execute(SysNoticeReadCreateRequest param) {
                SysNoticeRead sysNoticeRead = sysNoticeReadManager.createSysNoticeRead(SysNoticeReadAssembler.toModel(param));
                return SysNoticeReadAssembler.toResponse(sysNoticeRead);
            }

            @Override
            public void afterService(SysNoticeReadCreateRequest param, SysNoticeReadResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询公告已读记录。
     *
     * @param id 公告已读记录 ID
     * @return 公告已读记录信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysNoticeReadResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysNoticeReadParamChecker.checkId(param);
            }

            @Override
            public SysNoticeReadResponse execute(Long param) {
                SysNoticeRead sysNoticeRead = sysNoticeReadManager.getSysNoticeRead(param);
                AiPlatformInvoker.throwErrWhenNull(sysNoticeRead, ErrorCodeEnum.RESOURCE_NOT_FOUND, "公告已读记录不存在");
                return SysNoticeReadAssembler.toResponse(sysNoticeRead);
            }

            @Override
            public void afterService(Long param, SysNoticeReadResponse result) {
            }
        });
    }

    /**
     * 分页查询公告已读记录。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysNoticeReadResponse>> page(SysNoticeReadQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysNoticeReadQueryRequest param) {
                SysNoticeReadParamChecker.checkSysNoticeReadQueryRequest(param);
            }

            @Override
            public PageResult<SysNoticeReadResponse> execute(SysNoticeReadQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysNoticeReadQueryRequest());
                PageResult<SysNoticeRead> page = sysNoticeReadManager.pageSysNoticeReads(SysNoticeReadAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysNoticeReadAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新公告已读记录（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      公告已读记录 ID
     * @param request 更新内容
     * @return 更新后的公告已读记录信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysNoticeReadUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysNoticeReadUpdateRequest param) {
                SysNoticeReadParamChecker.checkId(id);
                SysNoticeReadParamChecker.checkSysNoticeReadUpdateRequest(param);
            }

            @Override
            public void execute(SysNoticeReadUpdateRequest param) {
                sysNoticeReadManager.updateSysNoticeRead(SysNoticeReadAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除公告已读记录。
     *
     * @param id 公告已读记录 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysNoticeReadParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysNoticeReadManager.deleteSysNoticeRead(id);
            }
        });
    }
}
