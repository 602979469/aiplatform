package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.integration.minio.MinioStorage;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.dto.FileThumbnailView;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import com.jakt.aiplatform.core.service.FileInfoService;
import com.jakt.aiplatform.core.service.checker.FileInfoBizChecker;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ImageUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 文件信息表领域服务实现：文件内容存 MinIO 对象存储，DB 只存元数据。
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {

    /** 文件信息表仓储。 */
    private final FileInfoRepository fileInfoRepository;

    /** 文件业务检查器。 */
    private final FileInfoBizChecker fileInfoBizChecker;

    /** MinIO 对象存储。 */
    private final MinioStorage minioStorage;

    public FileInfoServiceImpl(FileInfoRepository fileInfoRepository,
                               FileInfoBizChecker fileInfoBizChecker,
                               MinioStorage minioStorage) {
        this.fileInfoRepository = fileInfoRepository;
        this.fileInfoBizChecker = fileInfoBizChecker;
        this.minioStorage = minioStorage;
    }

    @Override
    public FileInfo upload(String namespace, byte[] content, String originalName, String remark) {
        return upload(namespace, content, originalName, remark, null);
    }

    @Override
    public FileInfo upload(String namespace, byte[] content, String originalName, String remark, String category) {
        return uploadStream(namespace, new ByteArrayInputStream(content), content.length, originalName, remark, category);
    }

    @Override
    public FileInfo uploadStream(String namespace, InputStream content, long size, String originalName, String remark) {
        return uploadStream(namespace, content, size, originalName, remark, null);
    }

    @Override
    public FileInfo uploadStream(String namespace, InputStream content, long size, String originalName,
                                 String remark, String category) {
        String fileType = StrUtil.nullToEmpty(FileNameUtil.extName(originalName)).toLowerCase();
        // 图片落库前压缩：手机直出/截图动辄几 MB，压到肉眼够用即可（列表/详情都不需要原图）
        byte[] uploadBytes = null;
        if (isCompressibleImage(fileType, size)) {
            byte[] sourceBytes = IoUtil.readBytes(content);
            byte[] compressed = ImageUtil.compressForStorage(sourceBytes, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT,
                    IMAGE_JPEG_QUALITY);
            if (ObjectUtil.isNotNull(compressed)) {
                originalName = toJpegName(originalName);
                fileType = JPEG_EXTENSION;
                size = compressed.length;
                uploadBytes = compressed;
            } else {
                // 压不动（已经很小或是异形图）：仍用原字节，避免流被读空
                uploadBytes = sourceBytes;
            }
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setNamespace(namespace);
        fileInfo.setOriginalName(originalName);
        fileInfo.setObjectKey(buildObjectKey(namespace));
        fileInfo.setFileSize(size);
        fileInfo.setFileType(fileType);
        fileInfo.setCategory(StrUtil.nullToEmpty(category));
        fileInfo.setRemark(remark);
        fileInfo.setCreateBy(Convert.toStr(UserContext.getUserId(), ""));
        fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
        InputStream uploadStream = ObjectUtil.isNotNull(uploadBytes) ? new ByteArrayInputStream(uploadBytes) : content;
        minioStorage.putObject(fileInfo.getObjectKey(), uploadStream, size,
                resolveContentType(fileInfo.getFileType()));
        return fileInfoRepository.insert(fileInfo);
    }

    @Override
    public PageResult<FileInfo> findPage(FileInfoQueryParam query) {
        return fileInfoRepository.findPage(query);
    }

    /** 缩略图内存缓存：key=fileId:width，1 小时过期、最多 500 张，避免重复缩放吃 CPU。 */
    private final LRUCache<String, byte[]> thumbnailCache = CacheUtil.newLRUCache(500, 60 * 60 * 1000L);

    /** 小于该大小的图片不压缩（收益不大）。 */
    private static final long IMAGE_COMPRESS_MIN_BYTES = 300 * 1024L;

    /** 超过该大小的图片不放进内存压缩（避免大图打爆堆）。 */
    private static final long IMAGE_COMPRESS_MAX_BYTES = 30 * 1024 * 1024L;

    /** 压缩后最大宽度：手机截图宽度一般不超过 1600，因此截图只重编码不缩尺寸，字依然清晰。 */
    private static final int IMAGE_MAX_WIDTH = 1600;

    /** 压缩后最大高度。 */
    private static final int IMAGE_MAX_HEIGHT = 4000;

    /** JPEG 质量：0.86 肉眼基本无感，体积通常降到原图 1/5 ~ 1/20。 */
    private static final float IMAGE_JPEG_QUALITY = 0.86F;

    /** 压缩后统一使用的扩展名。 */
    private static final String JPEG_EXTENSION = "jpg";

    @Override
    public FileThumbnailView getThumbnail(Long id, String namespace, Integer width) {
        FileInfo fileInfo = getFile(id, namespace);
        FileThumbnailView view = new FileThumbnailView();
        // 内容变化（重传/替换）时 fileSize 会变，ETag 随之变化
        view.setEtag("\"" + fileInfo.getId() + "-" + width + "-" + fileInfo.getFileSize() + "\"");
        String cacheKey = id + ":" + width;
        byte[] cached = thumbnailCache.get(cacheKey, false);
        if (ObjectUtil.isNotNull(cached)) {
            view.setContent(cached);
            return view;
        }
        try (InputStream inputStream = openContentStream(id, namespace)) {
            byte[] resized = ImageUtil.resizeToWidth(IoUtil.readBytes(inputStream), width);
            if (ObjectUtil.isNull(resized)) {
                // 不是图片或缩放失败：content 为空，由调用方回退原图
                return view;
            }
            thumbnailCache.put(cacheKey, resized);
            view.setContent(resized);
            return view;
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "缩略图生成失败 id={} width={}：{}", id, width, e.getMessage());
            return view;
        }
    }

    /**
     * 是否是值得压缩的图片：常见图片扩展名，且体积在阈值区间内。
     */
    private boolean isCompressibleImage(String fileType, long size) {
        if (size < IMAGE_COMPRESS_MIN_BYTES || size > IMAGE_COMPRESS_MAX_BYTES) {
            return false;
        }
        return StrUtil.equalsAny(fileType, "jpg", "jpeg", "png", "bmp", "webp", "heic", "heif");
    }

    /**
     * 压缩后把扩展名换成 .jpg（内容就是 JPEG，扩展名保持一致，下载/预览的类型才对得上）。
     */
    private String toJpegName(String originalName) {
        return FileNameUtil.mainName(originalName) + "." + JPEG_EXTENSION;
    }

    @Override
    public FileInfo getFile(Long id, String namespace) {
        FileInfo fileInfo = fileInfoRepository.findById(id);
        fileInfoBizChecker.checkFile(fileInfo, namespace);
        return fileInfo;
    }

    @Override
    public InputStream openContentStream(Long id, String namespace) {
        FileInfo fileInfo = getFile(id, namespace);
        AssertUtil.throwErrWhenBlank(fileInfo.getObjectKey(), BizErrorCodeEnum.FILE_NOT_FOUND, "文件不存在");
        return minioStorage.getObject(fileInfo.getObjectKey());
    }

    @Override
    public long getContentSize(Long id, String namespace) {
        FileInfo fileInfo = getFile(id, namespace);
        AssertUtil.throwErrWhenBlank(fileInfo.getObjectKey(), BizErrorCodeEnum.FILE_NOT_FOUND, "文件不存在");
        return minioStorage.statSize(fileInfo.getObjectKey());
    }

    @Override
    public void updateMeta(Long id, String namespace, String originalName, String remark) {
        updateMeta(id, namespace, originalName, remark, null);
    }

    @Override
    public void updateMeta(Long id, String namespace, String originalName, String remark, String category) {
        FileInfo fileInfo = getFile(id, namespace);
        if (StrUtil.isNotBlank(originalName)) {
            fileInfo.setOriginalName(originalName);
        }
        if (StrUtil.isNotBlank(remark)) {
            fileInfo.setRemark(remark);
        }
        // 素材库分类：只支持设置/修改，暂不支持清空（updateByCondition 只更新非空字段）
        if (StrUtil.isNotBlank(category)) {
            fileInfo.setCategory(category);
        }
        fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
        int affected = fileInfoRepository.updateByCondition(fileInfo);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void delete(Long id, String namespace) {
        FileInfo fileInfo = getFile(id, namespace);
        if (StrUtil.isNotBlank(fileInfo.getObjectKey())) {
            minioStorage.removeObject(fileInfo.getObjectKey());
        }
        int affected = fileInfoRepository.deleteById(id);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }

    @Override
    public FileInfo replace(Long id, String namespace, byte[] content, String originalName) {
        FileInfo fileInfo = getFile(id, namespace);
        AssertUtil.throwErrWhenBlank(fileInfo.getObjectKey(), BizErrorCodeEnum.FILE_NOT_FOUND, "文件不存在");
        minioStorage.putObject(fileInfo.getObjectKey(), new ByteArrayInputStream(content), content.length,
                resolveContentType(StrUtil.nullToEmpty(FileNameUtil.extName(originalName)).toLowerCase()));
        fileInfo.setOriginalName(originalName);
        fileInfo.setFileSize((long) content.length);
        fileInfo.setFileType(StrUtil.nullToEmpty(FileNameUtil.extName(originalName)).toLowerCase());
        fileInfo.setUpdateBy(Convert.toStr(UserContext.getUserId(), ""));
        int affected = fileInfoRepository.update(fileInfo);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
        return fileInfo;
    }

    /**
     * 生成 MinIO 对象键：files/{namespace}/{uuid}（改名/替换不影响对象键）。
     *
     * @param namespace 业务命名空间
     * @return 对象键
     */
    private String buildObjectKey(String namespace) {
        return "files/" + namespace + "/" + IdUtil.fastSimpleUUID();
    }

    /**
     * 按扩展名解析 Content-Type。
     *
     * @param fileType 扩展名（小写，不含点）
     * @return Content-Type
     */
    private String resolveContentType(String fileType) {
        if ("jpg".equals(fileType) || "jpeg".equals(fileType)) {
            return "image/jpeg";
        }
        if ("png".equals(fileType)) {
            return "image/png";
        }
        if ("gif".equals(fileType)) {
            return "image/gif";
        }
        if ("webp".equals(fileType)) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}
