package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.FileInfoDO;
import com.jakt.aiplatform.common.dal.query.FileInfoDalQuery;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;

/**
 * 文件信息表 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型；QueryParam（core-model）→ DalQuery（common-dal）
 * 在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class FileInfoConvertor {

    private FileInfoConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param source 文件信息表数据对象；为空返回 null
     * @return 文件信息表领域模型
     */
    public static FileInfo toModel(FileInfoDO source) {
        if (source == null) {
            return null;
        }
        FileInfo target = new FileInfo();
        target.setId(source.getId());
        target.setNamespace(source.getNamespace());
        target.setOriginalName(source.getOriginalName());
        target.setFileContent(source.getFileContent());
        target.setFileSize(source.getFileSize());
        target.setFileType(source.getFileType());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param source 文件信息表领域模型
     * @return 文件信息表数据对象
     */
    public static FileInfoDO toDO(FileInfo source) {
        FileInfoDO target = new FileInfoDO();
        target.setId(source.getId());
        target.setNamespace(source.getNamespace());
        target.setOriginalName(source.getOriginalName());
        target.setFileContent(source.getFileContent());
        target.setFileSize(source.getFileSize());
        target.setFileType(source.getFileType());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 文件信息表查询参数；为空返回空对象
     * @return 文件信息表查询参数（common-dal）
     */
    public static FileInfoDalQuery toDalQuery(FileInfoQueryParam source) {
        FileInfoDalQuery target = new FileInfoDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setNamespace(source.getNamespace());
        target.setOriginalName(source.getOriginalName());
        return target;
    }
}
