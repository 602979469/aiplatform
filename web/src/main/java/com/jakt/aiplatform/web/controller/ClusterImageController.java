package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.ClusterImageManager;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.web.assembler.ClusterImageAssembler;
import com.jakt.aiplatform.web.checker.ClusterImageParamChecker;
import com.jakt.aiplatform.web.param.ClusterImageCreateRequest;
import com.jakt.aiplatform.web.param.ClusterImageQueryRequest;
import com.jakt.aiplatform.web.param.ClusterImageUpdateRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.ClusterImageResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
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
 * 镜像管理接口。
 */
@RestController
@RequestMapping("/api/cluster/image")
public class ClusterImageController {

    private final ClusterImageManager clusterImageManager;

    public ClusterImageController(ClusterImageManager clusterImageManager) {
        this.clusterImageManager = clusterImageManager;
    }

    /** 镜像分页查询。 */
    @GetMapping("/page")
    public ApiResult<PageResult<ClusterImageResponse>> page(ClusterImageQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterImageQueryRequest, PageResult<ClusterImageResponse>>() {

            @Override
            public void beforeService(ClusterImageQueryRequest param) {
                ClusterImageParamChecker.checkQueryRequest(param);
            }

            @Override
            public PageResult<ClusterImageResponse> execute(ClusterImageQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new ClusterImageQueryRequest());
                PageResult<ClusterImage> page = clusterImageManager.pageClusterImages(ClusterImageAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, ClusterImageAssembler::toResponse);
            }
        });
    }

    /** 已发布镜像下拉列表（pod 配置绑定用）。 */
    @GetMapping("/options")
    public ApiResult<List<ClusterImageResponse>> options() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<ClusterImageResponse>>() {

            @Override
            public List<ClusterImageResponse> execute(Object param) {
                return ClusterImageAssembler.toResponseList(clusterImageManager.listPublishedImages());
            }
        });
    }

    /** 镜像详情。 */
    @GetMapping("/{id}")
    public ApiResult<ClusterImageResponse> detail(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<Long, ClusterImageResponse>() {

            @Override
            public void beforeService(Long param) {
                ClusterImageParamChecker.checkId(param);
            }

            @Override
            public ClusterImageResponse execute(Long param) {
                return ClusterImageAssembler.toResponse(clusterImageManager.getClusterImage(param));
            }
        });
    }

    /** 创建镜像（草稿）。 */
    @PostMapping
    public ApiResult<ClusterImageResponse> create(@RequestBody ClusterImageCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterImageCreateRequest, ClusterImageResponse>() {

            @Override
            public void beforeService(ClusterImageCreateRequest param) {
                ClusterImageParamChecker.checkCreateRequest(param);
            }

            @Override
            public ClusterImageResponse execute(ClusterImageCreateRequest param) {
                ClusterImage created = clusterImageManager.createClusterImage(ClusterImageAssembler.toModel(param));
                return ClusterImageAssembler.toResponse(created);
            }
        });
    }

    /** 修改镜像（仅草稿/构建失败）。 */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody ClusterImageUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<ClusterImageUpdateRequest>() {

            @Override
            public void beforeService(ClusterImageUpdateRequest param) {
                ClusterImageParamChecker.checkId(id);
                ClusterImageParamChecker.checkUpdateRequest(param);
            }

            @Override
            public void execute(ClusterImageUpdateRequest param) {
                clusterImageManager.updateClusterImage(ClusterImageAssembler.toModel(param, id));
            }
        });
    }

    /** 删除镜像（草稿/失败直接删；已发布物理删除）。 */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long param) {
                ClusterImageParamChecker.checkId(param);
            }

            @Override
            public void execute(Long param) {
                clusterImageManager.deleteClusterImage(param);
            }
        });
    }

    /** 提交构建（仅草稿/构建失败）。 */
    @PostMapping("/{id}/build")
    public ApiResult<Void> build(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long param) {
                ClusterImageParamChecker.checkId(param);
            }

            @Override
            public void execute(Long param) {
                clusterImageManager.buildClusterImage(param);
            }
        });
    }
}
