package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysPostDO;
import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.enums.PostStatusEnum;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjectUtil;


/**
 * 岗位 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysPostConvertor {

    private SysPostConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code、String 排序转 Integer，显式赋值）。
     *
     * @param post 岗位领域模型
     * @return 岗位查询参数
     */
    public static SysPostQueryParam toQueryParam(SysPost post) {
        SysPostQueryParam query = new SysPostQueryParam();
        query.setPostId(post.getPostId());
        query.setPostCode(post.getPostCode());
        query.setPostName(post.getPostName());
        query.setPostSort(StrUtil.isBlank(post.getPostSort()) ? null : Convert.toInt(post.getPostSort()));
        query.setStatus(post.getStatus() == null ? null : post.getStatus().getCode());
        query.setRemark(post.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 岗位数据对象（条件载体）
     * @return 岗位查询参数
     */
    public static SysPostQueryParam toQueryParam(SysPostDO condition) {
        SysPostQueryParam query = new SysPostQueryParam();
        query.setPostId(condition.getPostId());
        query.setPostCode(condition.getPostCode());
        query.setPostName(condition.getPostName());
        query.setPostSort(condition.getPostSort());
        query.setStatus(condition.getStatus());
        query.setRemark(condition.getRemark());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysPostDO 岗位数据对象；为空返回 null
     * @return 岗位领域模型
     */
    public static SysPost toModel(SysPostDO source) {
        if (source == null) {
            return null;
        }
        SysPost target = new SysPost();
        target.setPostId(source.getPostId());
        target.setPostCode(source.getPostCode());
        target.setPostName(source.getPostName());
        target.setPostSort(Convert.toStr(source.getPostSort()));
        target.setStatus(PostStatusEnum.fromCode(source.getStatus()));
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
     * @param sysPost 岗位领域模型
     * @return 岗位数据对象
     */
    public static SysPostDO toDO(SysPost source) {
        SysPostDO target = new SysPostDO();
        target.setPostId(source.getPostId());
        target.setPostCode(source.getPostCode());
        target.setPostName(source.getPostName());
        target.setPostSort(Convert.toInt(source.getPostSort()));
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
