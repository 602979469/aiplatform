package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.constant.FileConstants;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import com.jakt.aiplatform.core.service.FileInfoService;
import com.jakt.aiplatform.core.service.checker.FileInfoBizChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 文件信息表领域服务实现：磁盘读写 + 元数据编排。
 *
 * <p>存储布局：{fileRoot}/{namespace}/{storageName}；DB 只存元数据，不存文件内容。
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {

    /** 文件信息表仓储。 */
    private final FileInfoRepository fileInfoRepository;

    /** 文件业务检查器。 */
    private final FileInfoBizChecker fileInfoBizChecker;

    /** 文件存储根目录。 */
    private final String fileRoot;

    public FileInfoServiceImpl(FileInfoRepository fileInfoRepository,
                               FileInfoBizChecker fileInfoBizChecker,
                               @Value("${aiplatform.upload.file-root:" + FileConstants.DEFAULT_FILE_ROOT + "}") String fileRoot) {
        this.fileInfoRepository = fileInfoRepository;
        this.fileInfoBizChecker = fileInfoBizChecker;
        this.fileRoot = fileRoot;
    }

    @Override
    public FileInfo upload(String namespace, byte[] content, String originalName, String remark) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setNamespace(namespace);
        fileInfo.setOriginalName(originalName);
        fileInfo.setStorageName(IdUtil.fastSimpleUUID());
        fileInfo.setFileSize((long) content.length);
        fileInfo.setFileType(StrUtil.nullToEmpty(FileNameUtil.extName(originalName)).toLowerCase());
        fileInfo.setRemark(remark);
        fileInfo.setCreateBy(Convert.toStr(UserContext.getUserId(), ""));
        fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
        File target = resolve(namespace, fileInfo.getStorageName());
        try {
            FileUtil.mkdir(target.getParentFile());
            FileUtil.writeBytes(content, target);
            fileInfoRepository.insert(fileInfo);
        } catch (Exception e) {
            FileUtil.del(target);
            throw e;
        }
        return fileInfo;
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
    public File resolveFile(Long id, String namespace) {
        FileInfo fileInfo = getFile(id, namespace);
        File file = resolve(fileInfo.getNamespace(), fileInfo.getStorageName());
        AssertUtil.throwErrWhenFalse(file.isFile(), BizErrorCodeEnum.FILE_NOT_FOUND, "文件不存在");
        return file;
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
        int affected = fileInfoRepository.update(fileInfo);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void delete(Long id, String namespace) {
        FileInfo fileInfo = getFile(id, namespace);
        File target = resolve(fileInfo.getNamespace(), fileInfo.getStorageName());
        // 磁盘文件已不存在视为删除成功（幂等），删除失败则 DB 保留，可重试
        boolean diskDeleted = !target.exists() || FileUtil.del(target);
        AssertUtil.throwErrWhenFalse(diskDeleted, BizErrorCodeEnum.DELETE_FAILED, "磁盘文件删除失败");
        int affected = fileInfoRepository.deleteById(id);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }

    @Override
    public FileInfo replace(Long id, String namespace, byte[] content, String originalName) {
        FileInfo fileInfo = getFile(id, namespace);
        String oldStorageName = fileInfo.getStorageName();
        File newFile = resolve(namespace, IdUtil.fastSimpleUUID());
        try {
            FileUtil.mkdir(newFile.getParentFile());
            FileUtil.writeBytes(content, newFile);
            fileInfo.setStorageName(newFile.getName());
            fileInfo.setOriginalName(originalName);
            fileInfo.setFileSize((long) content.length);
            fileInfo.setFileType(StrUtil.nullToEmpty(FileNameUtil.extName(originalName)).toLowerCase());
            fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
            int affected = fileInfoRepository.update(fileInfo);
            AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
        } catch (Exception e) {
            FileUtil.del(newFile);
            throw e;
        }
        File oldFile = resolve(namespace, oldStorageName);
        if (oldFile.exists() && !FileUtil.del(oldFile)) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "文件内容替换后旧文件删除失败 id={} storageName={}",
                    id, oldStorageName);
        }
        return fileInfo;
    }

    /**
     * 拼接磁盘文件路径：{fileRoot}/{namespace}/{storageName}。
     *
     * @param namespace   业务命名空间
     * @param storageName 存储文件名
     * @return 磁盘文件
     */
    private File resolve(String namespace, String storageName) {
        return FileUtil.file(fileRoot, namespace, storageName);
    }
}
