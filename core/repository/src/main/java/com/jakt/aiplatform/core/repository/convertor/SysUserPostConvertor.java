package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysUserPostDO;
import com.jakt.aiplatform.core.model.domain.SysUserPost;


/**
 * 用户岗位关联 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysUserPostConvertor {

    private SysUserPostConvertor() {
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
