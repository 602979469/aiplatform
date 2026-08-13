package com.jakt.aiplatform.common.util.tools;

import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 镜像本地文件工具：文件名生成、本地已下载镜像查询。搜索与下载两个 Service 共用。
 */
public final class MirrorFileUtil {

    /** 本地镜像文件目录。 */
    public static final String IMAGE_DIR = "/tmp/ruoyi/images";

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
     * 本地文件是否存在。
     *
     * @param fileName 文件名
     * @return 是否存在
     */
    public static boolean isFileExists(String fileName) {
        if (StrUtil.isBlank(fileName) || fileName.contains("..") || fileName.contains("/")) {
            return false;
        }
        return Files.exists(Paths.get(IMAGE_DIR, fileName));
    }

    /**
     * 查询本地已下载的镜像文件（repo/tag），用于搜索时减少候选厂商。
     *
     * @param baseName 规范仓库名（如 library/mysql）
     * @param tag      期望版本（如 8、latest）
     * @return [repo, tag] 列表
     */
    public static List<String[]> findExistingImages(String baseName, String tag) {
        List<String[]> found = new ArrayList<>();
        File dir = new File(IMAGE_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".tar"));
        if (files == null) {
            return found;
        }
        String baseSegment = baseName.substring(baseName.lastIndexOf('/') + 1);
        for (File file : files) {
            String body = file.getName().substring(0, file.getName().length() - 4);
            int idx = body.lastIndexOf('_');
            if (idx <= 0) {
                continue;
            }
            String repo = body.substring(0, idx).replace('_', '/');
            String fileTag = body.substring(idx + 1);
            String repoSegment = repo.substring(repo.lastIndexOf('/') + 1);
            if (repoSegment.equals(baseSegment) && isTagMatch(fileTag, tag)) {
                found.add(new String[] { repo, fileTag });
            }
        }
        return found;
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

    /**
     * 创建本地镜像文件目录。
     */
    public static void ensureImageDir() {
        try {
            Files.createDirectories(Paths.get(IMAGE_DIR));
        } catch (Exception e) {
            throw new IllegalStateException("创建镜像存储目录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 校验文件名合法（防目录穿越）。
     *
     * @param fileName 文件名
     * @return 是否合法
     */
    public static boolean isValidFileName(String fileName) {
        return StrUtil.isNotBlank(fileName) && !fileName.contains("..")
                && !fileName.contains("/") && !fileName.contains("\\");
    }
}
