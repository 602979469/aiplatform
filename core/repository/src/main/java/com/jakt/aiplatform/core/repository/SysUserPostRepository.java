package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysUserPost;

import java.util.List;

/**
 * 用户岗位关联仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysUserPostRepository {

    /**
     * 按用户ID删除关联。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserPostByUserId(Long userId);

    /**
     * 按岗位ID统计关联数量。
     *
     * @param postId 岗位ID
     * @return 关联数量
     */
    int countUserPostById(Long postId);

    /**
     * 按用户ID集合批量删除关联。
     *
     * @param ids 用户ID数组
     * @return 影响行数
     */
    int deleteUserPost(Long[] ids);

    /**
     * 批量新增用户岗位关联。
     *
     * @param userPostList 用户岗位关联列表
     * @return 影响行数
     */
    int batchUserPost(List<SysUserPost> userPostList);
}
