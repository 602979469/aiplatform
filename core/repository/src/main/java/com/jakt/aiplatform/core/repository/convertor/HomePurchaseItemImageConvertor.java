package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.HomePurchaseItemImageDO;
import com.jakt.aiplatform.common.dal.query.HomePurchaseItemImageDalQuery;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItemImage;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemImageQueryParam;


/**
 * 家庭装修采购项图片 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class HomePurchaseItemImageConvertor {

    private HomePurchaseItemImageConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param homePurchaseItemImageDO 家庭装修采购项图片数据对象；为空返回 null
     * @return 家庭装修采购项图片领域模型
     */
    public static HomePurchaseItemImage toModel(HomePurchaseItemImageDO source) {
        if (source == null) {
            return null;
        }
        HomePurchaseItemImage target = new HomePurchaseItemImage();
        target.setId(source.getId());
        target.setItemId(source.getItemId());
        target.setFileId(source.getFileId());
        target.setOrderNum(source.getOrderNum());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param homePurchaseItemImage 家庭装修采购项图片领域模型
     * @return 家庭装修采购项图片数据对象
     */
    public static HomePurchaseItemImageDO toDO(HomePurchaseItemImage source) {
        HomePurchaseItemImageDO target = new HomePurchaseItemImageDO();
        target.setId(source.getId());
        target.setItemId(source.getItemId());
        target.setFileId(source.getFileId());
        target.setOrderNum(source.getOrderNum());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 家庭装修采购项图片查询参数；为空返回空对象
     * @return 家庭装修采购项图片查询参数（common-dal）
     */
    public static HomePurchaseItemImageDalQuery toDalQuery(HomePurchaseItemImageQueryParam source) {
        HomePurchaseItemImageDalQuery target = new HomePurchaseItemImageDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setItemId(source.getItemId());
        target.setItemIds(source.getItemIds());
        target.setFileId(source.getFileId());
        target.setOrderNum(source.getOrderNum());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
