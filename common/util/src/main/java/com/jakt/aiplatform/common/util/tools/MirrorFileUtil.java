package com.jakt.aiplatform.common.util.tools;

import cn.hutool.core.util.StrUtil;

/**
 * 镜像文件工具：tar 文件名生成、tag 匹配。镜像内容存数据库（docker_image 命名空间），不再落本地磁盘。
 */
public final class MirrorFileUtil {

    private MirrorFileUtil() {
    }

    /**
     * 文件名：repo 中的 / 替换为 _，如 bitnami_openjdk_17.tar。
     *
     * @param repo 仓库路径
     * @param tag  版本号
     * @return 本地文件名
     */
    public static String buildFileName(String repo, String tag) {
        String safeRepo = repo.replace('/', '_').replaceAll("[^a-zA-Z0-9._-]", "_");
        String safeTag = tag.replaceAll("[^a-zA-Z0-9._-]", "_");
        return safeRepo + "_" + safeTag + ".tar";
    }

    /**
     * tag 匹配：latest 精确匹配，其余按主版本前缀匹配。
     *
     * @param fileTag   本地文件 tag
     * @param expectTag 期望版本
     * @return 是否匹配
     */
    public static boolean isTagMatch(String fileTag, String expectTag) {
        if ("latest".equalsIgnoreCase(expectTag)) {
            return "latest".equals(fileTag);
        }
        return fileTag.equals(expectTag) || fileTag.startsWith(expectTag + ".")
                || fileTag.startsWith(expectTag + "-") || fileTag.startsWith(expectTag + "_");
    }

}
