package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.AiMirrorManager;
import com.jakt.aiplatform.web.assembler.AiMirrorAssembler;
import com.jakt.aiplatform.web.checker.AiMirrorParamChecker;
import com.jakt.aiplatform.web.param.MirrorDownloadRequest;
import com.jakt.aiplatform.web.param.MirrorSearchRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.MirrorDownloadTask;
import com.jakt.aiplatform.web.result.MirrorSearchResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 镜像加速器接口：搜索 / 生成下载 / 进度查询 / 文件下载。
 */
@RestController
@RequestMapping("/ai/mirror")
@Tag(name = "镜像加速器")
public class AiMirrorController {

    private final AiMirrorManager aiMirrorManager;

    public AiMirrorController(AiMirrorManager aiMirrorManager) {
        this.aiMirrorManager = aiMirrorManager;
    }

    /**
     * 搜索镜像。
     *
     * @param request         搜索请求
     * @param servletRequest  HttpServletRequest（取 User-Agent）
     * @return 搜索结果
     */
    @PostMapping("/search")
    public ApiResult<MirrorSearchResponse> search(@RequestBody MirrorSearchRequest request,
                                                         HttpServletRequest servletRequest) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<MirrorSearchRequest, MirrorSearchResponse>() {

            @Override
            public void beforeService(MirrorSearchRequest param) {
                AiMirrorParamChecker.checkSearch(param);
            }

            @Override
            public MirrorSearchResponse execute(MirrorSearchRequest param) {
                com.jakt.aiplatform.core.model.domain.MirrorSearchResponse result = aiMirrorManager.search(
                        param.getImageName(), param.getOs(), param.getArch(),
                        servletRequest.getHeader("User-Agent"));
                return AiMirrorAssembler.toSearchResponse(result);
            }
        });
    }

    /**
     * 生成下载（docker pull + docker save）。
     *
     * @param request 下载请求
     * @return 下载任务
     */
    @PostMapping("/download/generate")
    public ApiResult<MirrorDownloadTask> generate(@RequestBody MirrorDownloadRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<MirrorDownloadRequest, MirrorDownloadTask>() {

            @Override
            public void beforeService(MirrorDownloadRequest param) {
                AiMirrorParamChecker.checkGenerate(param);
            }

            @Override
            public MirrorDownloadTask execute(MirrorDownloadRequest param) {
                com.jakt.aiplatform.core.model.domain.MirrorDownloadTask task =
                        aiMirrorManager.generate(param.getRepo(), param.getTag());
                return AiMirrorAssembler.toDownloadTask(task);
            }
        });
    }

    /**
     * 查询生成进度。
     *
     * @param taskId 任务ID
     * @return 下载任务
     */
    @GetMapping("/download/status")
    public ApiResult<MirrorDownloadTask> status(@RequestParam String taskId) {
        return ApiTemplate.execute(taskId, new ApiTemplate.Callback<String, MirrorDownloadTask>() {

            @Override
            public void beforeService(String param) {
                AiMirrorParamChecker.checkTaskId(param);
            }

            @Override
            public MirrorDownloadTask execute(String param) {
                com.jakt.aiplatform.core.model.domain.MirrorDownloadTask task = aiMirrorManager.getStatus(param);
                return AiMirrorAssembler.toDownloadTask(task);
            }
        });
    }

}
