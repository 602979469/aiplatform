package com.jakt.aiplatform.web.util;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;

import cn.hutool.core.io.IoUtil;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 上传文件工具：MultipartFile 读取统一收口。
 */
public final class MultipartFileUtil {

    private MultipartFileUtil() {
    }

    /**
     * 读取上传文件字节。
     *
     * @param file 上传文件
     * @return 文件字节
     */
    public static byte[] readBytes(MultipartFile file) {
        try {
            return IoUtil.readBytes(file.getInputStream());
        } catch (IOException e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "上传文件读取失败");
        }
    }
}
