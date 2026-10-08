package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.domain.HomeMessage;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import com.jakt.aiplatform.core.repository.HomeMessageRepository;
import com.jakt.aiplatform.core.service.HomeMessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 首页留言板领域服务实现：身份、频率、内容规则都写在这里，持久化细节交给仓储。
 */
@Service
public class HomeMessageServiceImpl implements HomeMessageService {

    /** 首页留言板留言仓储。 */
    private final HomeMessageRepository homeMessageRepository;

    /** 文件信息表仓储：留言板头像素材来自 file_info（namespace=avatar）。 */
    private final FileInfoRepository fileInfoRepository;

    public HomeMessageServiceImpl(HomeMessageRepository homeMessageRepository, FileInfoRepository fileInfoRepository) {
        this.homeMessageRepository = homeMessageRepository;
        this.fileInfoRepository = fileInfoRepository;
    }

    @Override
    public HomeMessage postMessage(String content, String clientIp, String createBy) {
        String normalizedContent = StrUtil.trim(content);
        AssertUtil.throwErrWhenBlank(normalizedContent, ErrorCodeEnum.PARAM_INVALID, "留言内容不能为空");
        AssertUtil.throwErrWhenTrue(StrUtil.length(normalizedContent) > MAX_CONTENT_LENGTH,
                ErrorCodeEnum.PARAM_INVALID, "留言最多 " + MAX_CONTENT_LENGTH + " 字");

        String normalizedIp = StrUtil.nullToEmpty(clientIp);
        checkPostInterval(normalizedIp);

        HomeMessage homeMessage = new HomeMessage();
        homeMessage.setContent(normalizedContent);
        homeMessage.setClientIp(normalizedIp);
        homeMessage.setCreateBy(StrUtil.nullToEmpty(createBy));
        applyAnonymousIdentity(homeMessage, normalizedIp);
        return homeMessageRepository.insert(homeMessage);
    }

    @Override
    public PageResult<HomeMessage> findPage(int pageNum, int pageSize) {
        int safePageNum = Math.max(pageNum, PageConstants.DEFAULT_PAGE_NUM);
        int safePageSize = Math.min(Math.max(pageSize, PageConstants.DEFAULT_PAGE_NUM), PageConstants.MAX_PAGE_SIZE);
        return homeMessageRepository.findPage(safePageNum, safePageSize);
    }

    /**
     * 同一 IP 连续发帖间隔限制：防止刷屏（放在领域规则里，不依赖前端）。
     *
     * @param clientIp 客户端IP
     */
    private void checkPostInterval(String clientIp) {
        HomeMessage latest = homeMessageRepository.findLatestByIp(clientIp);
        if (latest == null || latest.getCreateTime() == null) {
            return;
        }
        long seconds = ChronoUnit.SECONDS.between(latest.getCreateTime(), LocalDateTime.now());
        AssertUtil.throwErrWhenTrue(seconds < POST_INTERVAL_SECONDS,
                ErrorCodeEnum.PARAM_INVALID, "发得太快啦，休息 " + POST_INTERVAL_SECONDS + " 秒再发～");
    }

    /**
     * 按 IP 分配匿名身份：昵称取头像素材文件名，配色由 IP 哈希决定，同一 IP 恒定。
     *
     * @param homeMessage 待落库的留言（就地写入头像/昵称/配色）
     * @param clientIp    客户端IP
     */
    private void applyAnonymousIdentity(HomeMessage homeMessage, String clientIp) {
        int hash = clientIp.hashCode() & 0x7fffffff;
        homeMessage.setColorIndex(hash % COLOR_COUNT);

        List<FileInfo> avatars = listAvatarFiles();
        if (CollUtil.isEmpty(avatars)) {
            homeMessage.setAvatarName(ANONYMOUS_NAME);
            return;
        }
        FileInfo avatar = avatars.get(hash % avatars.size());
        homeMessage.setAvatarFileId(avatar.getId());
        homeMessage.setAvatarName(StrUtil.blankToDefault(FileNameUtil.mainName(avatar.getOriginalName()), ANONYMOUS_NAME));
    }

    /**
     * 读取留言板头像素材（namespace=avatar 的全部文件，可在文件管理里增删）。
     *
     * @return 头像素材列表
     */
    private List<FileInfo> listAvatarFiles() {
        FileInfoQueryParam query = new FileInfoQueryParam();
        query.setNamespace(AVATAR_NAMESPACE);
        query.setPageSize(PageConstants.MAX_PAGE_SIZE);
        return fileInfoRepository.findPage(query).getDataList();
    }
}
