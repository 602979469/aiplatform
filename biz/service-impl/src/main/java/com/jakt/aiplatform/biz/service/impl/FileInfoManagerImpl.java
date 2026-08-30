package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.FileInfoManager;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.enums.FileNamespaceEnum;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;
import com.jakt.aiplatform.core.service.FileInfoService;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件管理业务编排：只做用例编排，输入输出为 core-model 领域对象。
 */
@Service
public class FileInfoManagerImpl implements FileInfoManager {

    /** 命名空间环境变量：逗号分隔，未配置时用枚举默认值。 */
    private static final String NAMESPACES_ENV = "AIPLATFORM_FILE_NAMESPACES";

    /** 文件领域服务。 */
    private final FileInfoService fileInfoService;

    public FileInfoManagerImpl(FileInfoService fileInfoService) {
        this.fileInfoService = fileInfoService;
    }

    @Override
    public List<String> listNamespaces() {
        String envNamespaces = System.getenv(NAMESPACES_ENV);
        if (StrUtil.isNotBlank(envNamespaces)) {
            return Arrays.stream(envNamespaces.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .distinct()
                    .toList();
        }
        List<String> defaults = new ArrayList<>();
        for (FileNamespaceEnum namespaceEnum : FileNamespaceEnum.values()) {
            defaults.add(namespaceEnum.getCode());
        }
        return defaults;
    }

    @Override
    public FileInfo upload(String namespace, byte[] content, String originalName, String remark) {
        checkNamespace(namespace);
        FileInfo created = fileInfoService.upload(namespace, content, originalName, remark);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "文件上传成功 id={} namespace={}", created.getId(), namespace);
        return created;
    }

    @Override
    public PageResult<FileInfo> page(FileInfoQueryParam query) {
        if (StrUtil.isNotBlank(query.getNamespace())) {
            checkNamespace(query.getNamespace());
        }
        return fileInfoService.findPage(query);
    }

    @Override
    public FileInfo getFile(Long id, String namespace) {
        return fileInfoService.getFile(id, namespace);
    }

    @Override
    public InputStream openContentStream(Long id, String namespace) {
        return fileInfoService.openContentStream(id, namespace);
    }

    @Override
    public long getContentSize(Long id, String namespace) {
        return fileInfoService.getContentSize(id, namespace);
    }

    @Override
    public void update(Long id, String namespace, String originalName, String remark) {
        fileInfoService.updateMeta(id, namespace, originalName, remark);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "文件元信息更新成功 id={} namespace={}", id, namespace);
    }

    @Override
    public void delete(Long id, String namespace) {
        fileInfoService.delete(id, namespace);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "文件删除成功 id={} namespace={}", id, namespace);
    }

    @Override
    public FileInfo replace(Long id, String namespace, byte[] content, String originalName) {
        FileInfo replaced = fileInfoService.replace(id, namespace, content, originalName);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "文件内容替换成功 id={} namespace={}", id, namespace);
        return replaced;
    }

    /**
     * 校验业务命名空间在可用列表内。
     *
     * @param namespace 业务命名空间
     */
    private void checkNamespace(String namespace) {
        AssertUtil.throwErrWhenBlank(namespace, ErrorCodeEnum.PARAM_INVALID, "业务命名空间不能为空");
        AssertUtil.throwErrWhenFalse(listNamespaces().contains(namespace),
                ErrorCodeEnum.PARAM_INVALID, "业务命名空间不在可用列表内");
    }
}
