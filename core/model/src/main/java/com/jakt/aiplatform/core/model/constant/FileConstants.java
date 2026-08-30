package com.jakt.aiplatform.core.model.constant;

/**
 * 文件管理模块常量。
 */
public final class FileConstants {

    /** namespace 合法格式：仅字母/数字/下划线/中划线，禁止点号（天然排除路径穿越）。 */
    public static final String NAMESPACE_PATTERN = "^[A-Za-z0-9_-]{1,64}$";

    /** 文件存储根目录默认值（bootstrap 可配置覆盖）。 */
    public static final String DEFAULT_FILE_ROOT = "./uploads/files";

    /** 头像访问路径前缀（对应 FileInfoController 的头像读取接口）。 */
    public static final String AVATAR_URL_PREFIX = "/api/file/avatar/";

    private FileConstants() {
    }
}
