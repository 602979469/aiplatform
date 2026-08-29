package com.jakt.aiplatform.core.service.checker;

import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import org.springframework.stereotype.Component;

/**
 * 文件信息表业务检查器：文件存在性 + namespace 归属校验，统一返回"文件不存在"。
 */
@Component
public class FileInfoBizChecker {

    /** 文件信息表仓储。 */
    private final FileInfoRepository fileInfoRepository;

    public FileInfoBizChecker(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    /**
     * 校验文件存在且属于指定 namespace。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     */
    public void checkFile(Long id, String namespace) {
        checkFile(fileInfoRepository.findById(id), namespace);
    }

    /**
     * 校验文件存在且属于指定 namespace；不匹配与不存在统一抛 FILE_NOT_FOUND，防探测。
     *
     * @param fileInfo  文件信息；为空视为不存在
     * @param namespace 业务命名空间
     */
    public void checkFile(FileInfo fileInfo, String namespace) {
        AssertUtil.throwErrWhenNull(fileInfo, BizErrorCodeEnum.FILE_NOT_FOUND, "文件不存在");
        AssertUtil.throwErrWhenFalse(fileInfo.getNamespace().equals(namespace),
                BizErrorCodeEnum.FILE_NOT_FOUND, "文件不存在");
    }
}
