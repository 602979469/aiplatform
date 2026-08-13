package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 镜像下载生成任务（内存态，不落库）。
 */
@Data
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

    /** 错误信息（透传底层 errormsg）。 */
    private String errorMsg;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 完成时间。 */
    private LocalDateTime finishTime;
}
