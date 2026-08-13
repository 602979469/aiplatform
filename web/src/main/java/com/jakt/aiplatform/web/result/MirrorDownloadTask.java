package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 镜像下载任务响应 DTO（内存态）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MirrorDownloadTask {

    /** 任务ID。 */
    private String taskId;

    /** 仓库路径。 */
    private String repo;

    /** 版本号/tag。 */
    private String tag;

    /** 本地文件名。 */
    private String fileName;

    /** 状态：generating 生成中 / ready 已完成 / failed 失败。 */
    private String status;

    /** 进度 0~100。 */
    private int progress;

    /** 进度提示。 */
    private String progressMsg;

    /** 错误码（TIMEOUT 超时 / UNKNOWN 未知）。 */
    private String errorCode;

    /** 错误信息。 */
    private String errorMsg;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 完成时间。 */
    private LocalDateTime finishTime;
}
