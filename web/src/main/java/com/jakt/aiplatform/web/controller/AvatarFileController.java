package com.jakt.aiplatform.web.controller;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Set;

/**
 * 头像文件读取接口。
 *
 * <p>文件流无法包 ApiResult，直接返回二进制；仅允许读取 avatarDir 内的文件，防目录穿越。
 */
@RestController
@RequestMapping("/uploads/avatar")
@Tag(name = "头像文件")
public class AvatarFileController {

    /** 允许的头像扩展名。 */
    private static final Set<String> AVATAR_EXTS = Set.of("png", "jpg", "jpeg", "gif", "webp");

    /** 头像存储目录。 */
    private final String avatarDir;

    public AvatarFileController(@Value("${aiplatform.upload.avatar-dir:./uploads/avatar}") String avatarDir) {
        this.avatarDir = avatarDir;
    }

    /**
     * 读取头像文件。
     *
     * @param fileName 文件名
     * @return 图片二进制
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> avatar(@PathVariable String fileName) {
        AssertUtil.throwErrWhenTrue(StrUtil.isBlank(fileName)
                        || !fileName.equals(FileUtil.getName(fileName))
                        || !AVATAR_EXTS.contains(FileNameUtil.extName(fileName).toLowerCase()),
                ErrorCodeEnum.PARAM_INVALID, "文件名不合法");
        File file = FileUtil.file(avatarDir, fileName);
        AssertUtil.throwErrWhenFalse(file.isFile(), BizErrorCodeEnum.RESOURCE_NOT_FOUND, "头像文件不存在");
        String ext = FileNameUtil.extName(fileName).toLowerCase();
        String mimeExt = "jpg".equals(ext) ? "jpeg" : ext;
        MediaType mediaType = MediaType.parseMediaType("image/" + mimeExt);
        return ResponseEntity.ok().contentType(mediaType).body(new FileSystemResource(file));
    }
}
