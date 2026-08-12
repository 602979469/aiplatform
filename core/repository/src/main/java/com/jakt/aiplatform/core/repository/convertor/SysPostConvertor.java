package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysPostDO;
import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.enums.PostStatusEnum;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;


/**
 * 岗位 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysPostConvertor {

    private SysPostConvertor() {
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
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
