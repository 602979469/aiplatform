package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import com.jakt.aiplatform.core.service.FileInfoService;
import com.jakt.aiplatform.core.service.checker.FileInfoBizChecker;
import org.springframework.stereotype.Service;

/**
 * 文件信息表领域服务实现：文件内容直接存数据库（LONGBLOB），元数据与内容一体。
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {

    /** 文件信息表仓储。 */
    private final FileInfoRepository fileInfoRepository;

    /** 文件业务检查器。 */
    private final FileInfoBizChecker fileInfoBizChecker;

    public FileInfoServiceImpl(FileInfoRepository fileInfoRepository,
                               FileInfoBizChecker fileInfoBizChecker) {
        this.fileInfoRepository = fileInfoRepository;
        this.fileInfoBizChecker = fileInfoBizChecker;
    }

    @Override
    public FileInfo upload(String namespace, byte[] content, String originalName, String remark) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setNamespace(namespace);
        fileInfo.setOriginalName(originalName);
        fileInfo.setFileContent(content);
        fileInfo.setFileSize((long) content.length);
        fileInfo.setFileType(StrUtil.nullToEmpty(FileNameUtil.extName(originalName)).toLowerCase());
        fileInfo.setRemark(remark);
        fileInfo.setCreateBy(Convert.toStr(UserContext.getUserId(), ""));
        fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
        return fileInfoRepository.insert(fileInfo);
    }

    @Override
    public PageResult<FileInfo> findPage(FileInfoQueryParam query) {
        return fileInfoRepository.findPage(query);
    }

    @Override
    public FileInfo getFile(Long id, String namespace) {
        FileInfo fileInfo = fileInfoRepository.findById(id);
        fileInfoBizChecker.checkFile(fileInfo, namespace);
        return fileInfo;
    }

    @Override
    public FileInfo getFileContent(Long id, String namespace) {
        FileInfo fileInfo = getFile(id, namespace);
        byte[] content = fileInfoRepository.findContent(id);
        AssertUtil.throwErrWhenNull(content, BizErrorCodeEnum.FILE_NOT_FOUND, "文件不存在");
        fileInfo.setFileContent(content);
        return fileInfo;
    }

    @Override
    public void updateMeta(Long id, String namespace, String originalName, String remark) {
        FileInfo fileInfo = getFile(id, namespace);
        if (StrUtil.isNotBlank(originalName)) {
            fileInfo.setOriginalName(originalName);
        }
        if (StrUtil.isNotBlank(remark)) {
            fileInfo.setRemark(remark);
        }
        fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
        int affected = fileInfoRepository.updateByCondition(fileInfo);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void delete(Long id, String namespace) {
        getFile(id, namespace);
        int affected = fileInfoRepository.deleteById(id);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }

    @Override
    public FileInfo replace(Long id, String namespace, byte[] content, String originalName) {
        FileInfo fileInfo = getFile(id, namespace);
        fileInfo.setFileContent(content);
        fileInfo.setOriginalName(originalName);
        fileInfo.setFileSize((long) content.length);
        fileInfo.setFileType(StrUtil.nullToEmpty(FileNameUtil.extName(originalName)).toLowerCase());
        fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
        int affected = fileInfoRepository.update(fileInfo);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
        return fileInfo;
    }
}
