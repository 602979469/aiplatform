package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserPostDO;
import com.jakt.aiplatform.core.model.domain.SysUserPost;
import com.jakt.aiplatform.core.model.param.SysUserPostQueryParam;

/**
 * 用户岗位关联 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserPostConvertor {

    private SysUserPostConvertor() {
    }

    /**
     * 领域模型 → 查询参数（显式赋值）。
     *
     * @param userPost 用户岗位关联领域模型
     * @return 用户岗位关联查询参数
     */
    public static SysUserPostQueryParam toQueryParam(SysUserPost userPost) {
        SysUserPostQueryParam query = new SysUserPostQueryParam();
        query.setId(userPost.getId());
        query.setUserId(userPost.getUserId());
        query.setPostId(userPost.getPostId());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 用户岗位关联数据对象（条件载体）
     * @return 用户岗位关联查询参数
     */
    public static SysUserPostQueryParam toQueryParam(SysUserPostDO condition) {
        SysUserPostQueryParam query = new SysUserPostQueryParam();
        query.setId(condition.getId());
        query.setUserId(condition.getUserId());
        query.setPostId(condition.getPostId());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysUserPostDO 用户岗位关联数据对象；为空返回 null
     * @return 用户岗位关联领域模型
     */
    public static SysUserPost toModel(SysUserPostDO source) {
        if (source == null) {
            return null;
        }
        SysUserPost target = new SysUserPost();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setPostId(source.getPostId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysUserPost 用户岗位关联领域模型
     * @return 用户岗位关联数据对象
     */
    public static SysUserPostDO toDO(SysUserPost source) {
        SysUserPostDO target = new SysUserPostDO();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setPostId(source.getPostId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
