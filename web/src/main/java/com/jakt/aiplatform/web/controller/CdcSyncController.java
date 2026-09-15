package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jakt.aiplatform.biz.service.CdcSyncManager;
import com.jakt.aiplatform.core.model.domain.CdcEsMapping;
import com.jakt.aiplatform.core.model.domain.CdcMappingPreview;
import com.jakt.aiplatform.core.model.domain.CdcSyncStatus;
import com.jakt.aiplatform.web.assembler.CdcSyncAssembler;
import com.jakt.aiplatform.web.checker.CdcSyncParamChecker;
import com.jakt.aiplatform.web.param.CdcMappingSaveRequest;
import com.jakt.aiplatform.web.param.CdcSqlFormatRequest;
import com.jakt.aiplatform.web.param.CdcTaskRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.CdcMappingPreviewResponse;
import com.jakt.aiplatform.web.result.CdcMappingResponse;
import com.jakt.aiplatform.web.result.CdcSyncStatusResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ES 同步（canal CDC）管理接口：映射配置的增删改查 + 全量导入 + 运行状态。
 */
@RestController
@RequestMapping("/api/cdc/sync")
public class CdcSyncController {

    /** ES 同步管理 Manager。 */
    private final CdcSyncManager cdcSyncManager;

    public CdcSyncController(CdcSyncManager cdcSyncManager) {
        this.cdcSyncManager = cdcSyncManager;
    }

    /** 映射列表。 */
    @GetMapping("/list")
    @SaCheckPermission("cdc:sync:list")
    public ApiResult<List<CdcMappingResponse>> list() {
        return ApiTemplate.execute(new Object(), new ApiTemplate.Callback<Object, List<CdcMappingResponse>>() {

            @Override
            public List<CdcMappingResponse> execute(Object param) {
                List<CdcEsMapping> mappings = cdcSyncManager.listMappings();
                return CdcSyncAssembler.toResponseList(mappings);
            }
        });
    }

    /** 映射详情。 */
    @GetMapping("/detail")
    @SaCheckPermission("cdc:sync:list")
    public ApiResult<CdcMappingResponse> detail(CdcTaskRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<CdcTaskRequest, CdcMappingResponse>() {

            @Override
            public void beforeService(CdcTaskRequest param) {
                CdcSyncParamChecker.checkTask(param);
            }

            @Override
            public CdcMappingResponse execute(CdcTaskRequest param) {
                CdcEsMapping mapping = cdcSyncManager.getMapping(param.getName());
                return CdcSyncAssembler.toResponse(mapping);
            }
        });
    }

    /** 预检 + yml 预览（创建指引）。 */
    @PostMapping("/preview")
    @SaCheckPermission("cdc:sync:list")
    public ApiResult<CdcMappingPreviewResponse> preview(@RequestBody CdcMappingSaveRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<CdcMappingSaveRequest,
                CdcMappingPreviewResponse>() {

            @Override
            public void beforeService(CdcMappingSaveRequest param) {
                CdcSyncParamChecker.checkSave(param);
            }

            @Override
            public CdcMappingPreviewResponse execute(CdcMappingSaveRequest param) {
                CdcEsMapping mapping = CdcSyncAssembler.toMapping(param);
                CdcMappingPreview preview = cdcSyncManager.preview(mapping);
                return CdcSyncAssembler.toPreviewResponse(preview);
            }
        });
    }

    /** 保存映射（新增或覆盖同名任务）。 */
    @PostMapping("/save")
    @SaCheckPermission("cdc:sync:edit")
    public ApiResult<Void> save(@RequestBody CdcMappingSaveRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<CdcMappingSaveRequest>() {

            @Override
            public void beforeService(CdcMappingSaveRequest param) {
                CdcSyncParamChecker.checkSave(param);
            }

            @Override
            public void execute(CdcMappingSaveRequest param) {
                CdcEsMapping mapping = CdcSyncAssembler.toMapping(param);
                cdcSyncManager.saveMapping(mapping, param.getRestart());
            }
        });
    }

    /** 删除映射。 */
    @PostMapping("/delete")
    @SaCheckPermission("cdc:sync:edit")
    public ApiResult<Void> delete(@RequestBody CdcTaskRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<CdcTaskRequest>() {

            @Override
            public void beforeService(CdcTaskRequest param) {
                CdcSyncParamChecker.checkTask(param);
            }

            @Override
            public void execute(CdcTaskRequest param) {
                cdcSyncManager.deleteMapping(param.getName(), param.getRestart());
            }
        });
    }

    /** SQL 格式化（补表别名、限定列名）。 */
    @PostMapping("/format")
    @SaCheckPermission("cdc:sync:list")
    public ApiResult<String> format(@RequestBody CdcSqlFormatRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<CdcSqlFormatRequest, String>() {

            @Override
            public void beforeService(CdcSqlFormatRequest param) {
                CdcSyncParamChecker.checkFormat(param);
            }

            @Override
            public String execute(CdcSqlFormatRequest param) {
                return cdcSyncManager.formatSql(param.getSql());
            }
        });
    }

    /** 触发全量导入（把 MySQL 存量数据补进 ES）。 */
    @PostMapping("/etl")
    @SaCheckPermission("cdc:sync:edit")
    public ApiResult<String> etl(@RequestBody CdcTaskRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<CdcTaskRequest, String>() {

            @Override
            public void beforeService(CdcTaskRequest param) {
                CdcSyncParamChecker.checkTask(param);
            }

            @Override
            public String execute(CdcTaskRequest param) {
                return cdcSyncManager.triggerEtl(param.getName());
            }
        });
    }

    /** 滚动重启 canal-adapter（让映射配置生效）。 */
    @PostMapping("/restart")
    @SaCheckPermission("cdc:sync:edit")
    public ApiResult<Void> restart() {
        return ApiTemplate.executeWithoutResult(new Object(),
                new ApiTemplate.CallbackWithoutResult<Object>() {

                    @Override
                    public void execute(Object param) {
                        cdcSyncManager.restartAdapter();
                    }
                });
    }

    /** 运行状态（适配器副本 + canal 实例）。 */
    @GetMapping("/status")
    @SaCheckPermission("cdc:sync:list")
    public ApiResult<CdcSyncStatusResponse> status() {
        return ApiTemplate.execute(new Object(), new ApiTemplate.Callback<Object, CdcSyncStatusResponse>() {

            @Override
            public CdcSyncStatusResponse execute(Object param) {
                CdcSyncStatus status = cdcSyncManager.status();
                return CdcSyncAssembler.toStatusResponse(status);
            }
        });
    }

    /** ES 业务索引列表（创建映射时选择目标索引）。 */
    @GetMapping("/indices")
    @SaCheckPermission("cdc:sync:list")
    public ApiResult<List<String>> indices() {
        return ApiTemplate.execute(new Object(), new ApiTemplate.Callback<Object, List<String>>() {

            @Override
            public List<String> execute(Object param) {
                return cdcSyncManager.listEsIndices();
            }
        });
    }
}
