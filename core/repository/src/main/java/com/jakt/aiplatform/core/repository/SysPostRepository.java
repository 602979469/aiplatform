package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysPost;

import java.util.List;

/**
 * 岗位仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysPostRepository {

    /**
     * 查询岗位列表。
     *
     * @param post 查询条件
     * @return 岗位列表
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 查询全部岗位。
     *
     * @return 岗位列表
     */
    List<SysPost> selectPostAll();

    /**
     * 按用户ID查询岗位列表。
     *
     * @param userId 用户ID
     * @return 岗位列表
     */
    List<SysPost> selectPostsByUserId(Long userId);

    /**
     * 按主键查询岗位。
     *
     * @param postId 岗位ID
     * @return 岗位领域模型
     */
    SysPost selectPostById(Long postId);

    /**
     * 校验岗位名称唯一。
     *
     * @param post 岗位（含 postId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkPostNameUnique(SysPost post);

    /**
     * 校验岗位编码唯一。
     *
     * @param post 岗位（含 postId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkPostCodeUnique(SysPost post);

    /**
     * 按 ID 集合批量删除。
     *
     * @param ids 岗位ID集合（逗号分隔）
     * @return 影响行数
     */
    int deletePostByIds(String ids);

    /**
     * 全量更新。
     *
     * @param post 岗位
     * @return 影响行数
     */
    int updatePost(SysPost post);

    /**
     * 新增。
     *
     * @param post 岗位
     * @return 影响行数
     */
    int insertPost(SysPost post);
}
