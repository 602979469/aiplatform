package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.biz.service.FileInfoManager;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.enums.FileNamespaceEnum;
import com.jakt.aiplatform.web.assembler.FileInfoAssembler;
import com.jakt.aiplatform.web.checker.FileInfoParamChecker;
import com.jakt.aiplatform.web.param.FileInfoQueryRequest;
import com.jakt.aiplatform.web.param.FileInfoUpdateRequest;
import com.jakt.aiplatform.web.param.FileUploadRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.FileInfoResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import com.jakt.aiplatform.web.util.MultipartFileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件管理接口：上传 / 列表 / 下载 / 更新 / 删除 / 替换。
 *
 * <p>Controller 只做收参 → ParamChecker → Manager → Assembler → ApiTemplate；
 * 下载为二进制流，无法包 ApiResult，直接写响应流（参照 AiMirrorController.download）。
 */
@RestController
@RequestMapping("/api/file")
@Tag(name = "文件管理")
public class FileInfoController {

    /** 文件管理 Manager。 */
    private final FileInfoManager fileInfoManager;

    public FileInfoController(FileInfoManager fileInfoManager) {
        this.fileInfoManager = fileInfoManager;
    }

    /**
     * 查询可用业务命名空间列表（下拉框数据源）。
     *
     * @return 命名空间列表
     */
    @GetMapping("/namespaces")
    @SaIgnore
    public ApiResult<List<String>> namespaces() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<String>>() {

            @Override
            public List<String> execute(Object param) {
                return fileInfoManager.listNamespaces();
            }
        });
    }

    /**
     * 上传文件（multipart：namespace + file + 可选 remark）。
     *
     * @param request 上传请求
     * @return 上传后的文件信息
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaIgnore
    public ApiResult<FileInfoResponse> upload(FileUploadRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<FileUploadRequest, FileInfoResponse>() {

            @Override
            public void beforeService(FileUploadRequest param) {
                FileInfoParamChecker.checkUpload(param);
            }

            @Override
            public FileInfoResponse execute(FileUploadRequest param) {
                FileInfo fileInfo = fileInfoManager.upload(param.getNamespace(),
                        MultipartFileUtil.readBytes(param.getFile()),
                        param.getFile().getOriginalFilename(), param.getRemark());
                return FileInfoAssembler.toResponse(fileInfo);
            }
        });
    }

    /**
     * 按 namespace 分页查询文件列表。
     *
     * @param request 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @SaIgnore
    public ApiResult<PageResult<FileInfoResponse>> page(FileInfoQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<FileInfoQueryRequest, PageResult<FileInfoResponse>>() {

            @Override
            public void beforeService(FileInfoQueryRequest param) {
                FileInfoParamChecker.checkPage(param);
            }

            @Override
            public PageResult<FileInfoResponse> execute(FileInfoQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new FileInfoQueryRequest());
                PageResult<FileInfo> page = fileInfoManager.page(FileInfoAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, FileInfoAssembler::toResponse);
            }
        });
    }

    /**
     * 下载文件（二进制流，无法包 ApiResult，直接写响应流）。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @param response  HttpServletResponse
     */
    @GetMapping("/{id}/download")
    @SaIgnore
    public void download(@PathVariable Long id, @RequestParam String namespace, HttpServletResponse response) throws Exception {
        FileInfoParamChecker.checkId(id);
        FileInfoParamChecker.checkNamespace(namespace);
        FileInfo fileInfo = fileInfoManager.getFile(id, namespace);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8));
        response.setContentLengthLong(fileInfoManager.getContentSize(id, namespace));
        try (InputStream inputStream = fileInfoManager.openContentStream(id, namespace);
             OutputStream outputStream = response.getOutputStream()) {
            IoUtil.copy(inputStream, outputStream);
            outputStream.flush();
        }
    }

    /**
     * 读取头像（inline 图片流，公开访问；头像统一存 user_avatar 命名空间）。
     *
     * <p>文件流无法包 ApiResult，直接写响应流；无 Content-Disposition attachment，保证浏览器直接渲染。
     *
     * @param id       文件主键
     * @param response HttpServletResponse
     */
    @GetMapping("/avatar/{id}")
    @SaIgnore
    public void avatar(@PathVariable Long id, HttpServletResponse response) throws Exception {
        FileInfoParamChecker.checkId(id);
        FileInfo fileInfo = fileInfoManager.getFile(id, FileNamespaceEnum.USER_AVATAR.getCode());
        response.setContentType(resolveImageContentType(fileInfo.getFileType()));
        response.setContentLengthLong(fileInfoManager.getContentSize(id, FileNamespaceEnum.USER_AVATAR.getCode()));
        try (InputStream inputStream = fileInfoManager.openContentStream(id, FileNamespaceEnum.USER_AVATAR.getCode());
             OutputStream outputStream = response.getOutputStream()) {
            IoUtil.copy(inputStream, outputStream);
            outputStream.flush();
        }
    }

    /**
     * 更新文件元信息（改名/备注）。
     *
     * @param id      文件主键
     * @param request 更新请求
     * @return 统一返回体
     */
    @PutMapping("/{id}")
    @SaIgnore
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody FileInfoUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<FileInfoUpdateRequest>() {

            @Override
            public void beforeService(FileInfoUpdateRequest param) {
                FileInfoParamChecker.checkId(id);
                FileInfoParamChecker.checkUpdate(param);
            }

            @Override
            public void execute(FileInfoUpdateRequest param) {
                fileInfoManager.update(id, param.getNamespace(), param.getOriginalName(), param.getRemark());
            }
        });
    }

    /**
     * 删除文件（物理删除 DB 行 + 磁盘文件）。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @return 统一返回体
     */
    @DeleteMapping("/{id}")
    @SaIgnore
    public ApiResult<Void> delete(@PathVariable Long id, @RequestParam String namespace) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long param) {
                FileInfoParamChecker.checkId(param);
                FileInfoParamChecker.checkNamespace(namespace);
            }

            @Override
            public void execute(Long param) {
                fileInfoManager.delete(param, namespace);
            }
        });
    }

    /**
     * 替换文件内容（保留记录，覆盖旧文件）。
     *
     * @param id      文件主键
     * @param request 上传请求（namespace + file）
     * @return 替换后的文件信息
     */
    @PostMapping(value = "/{id}/replace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaIgnore
    public ApiResult<FileInfoResponse> replace(@PathVariable Long id, FileUploadRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<FileUploadRequest, FileInfoResponse>() {

            @Override
            public void beforeService(FileUploadRequest param) {
                FileInfoParamChecker.checkId(id);
                FileInfoParamChecker.checkUpload(param);
            }

            @Override
            public FileInfoResponse execute(FileUploadRequest param) {
                FileInfo fileInfo = fileInfoManager.replace(id, param.getNamespace(),
                        MultipartFileUtil.readBytes(param.getFile()),
                        param.getFile().getOriginalFilename());
                return FileInfoAssembler.toResponse(fileInfo);
            }
        });
    }

    /**
     * 按文件扩展名解析图片 Content-Type。
     *
     * @param fileType 文件扩展名（小写，不含点）
     * @return Content-Type
     */
    private String resolveImageContentType(String fileType) {
        String type = StrUtil.nullToEmpty(fileType).toLowerCase();
        if ("jpg".equals(type) || "jpeg".equals(type)) {
            return "image/jpeg";
        }
        if ("png".equals(type)) {
            return "image/png";
        }
        if ("gif".equals(type)) {
            return "image/gif";
        }
        if ("webp".equals(type)) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}
