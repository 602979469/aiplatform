package com.jakt.aiplatform.core.model.domain;


import com.jakt.aiplatform.common.framework.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息表领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfo extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 业务命名空间（磁盘目录名，双重隔离）。 */
    private String namespace;

    /** 原始文件名（含扩展名，展示/下载用）。 */
    private String originalName;

    /** 存储文件名（UUID，磁盘实际文件名，不对外暴露）。 */
    private String storageName;

    /** 文件大小（字节）。 */
    private Long fileSize;

    /** 扩展名（小写，不含点）。 */
    private String fileType;

    /** 备注。 */
    private String remark;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

}
