package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.HomePurchaseItemDO;
import com.jakt.aiplatform.common.dal.query.HomePurchaseItemDalQuery;
import com.jakt.aiplatform.core.model.domain.HomePurchaseItem;
import com.jakt.aiplatform.core.model.param.HomePurchaseItemQueryParam;


/**
 * 家庭装修采购项 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class HomePurchaseItemConvertor {

    private HomePurchaseItemConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param homePurchaseItemDO 家庭装修采购项数据对象；为空返回 null
     * @return 家庭装修采购项领域模型
     */
    public static HomePurchaseItem toModel(HomePurchaseItemDO source) {
        if (source == null) {
            return null;
        }
        HomePurchaseItem target = new HomePurchaseItem();
        target.setId(source.getId());
        target.setBigTypeCode(source.getBigTypeCode());
        target.setBigTypeName(source.getBigTypeName());
        target.setTypeCode(source.getTypeCode());
        target.setTypeName(source.getTypeName());
        target.setProductName(source.getProductName());
        target.setQuantity(source.getQuantity());
        target.setBudgetText(source.getBudgetText());
        target.setBudgetMin(source.getBudgetMin());
        target.setBudgetMax(source.getBudgetMax());
        target.setInstallFee(source.getInstallFee());
        target.setRemark(source.getRemark());
        target.setUserId(source.getUserId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param homePurchaseItem 家庭装修采购项领域模型
     * @return 家庭装修采购项数据对象
     */
    public static HomePurchaseItemDO toDO(HomePurchaseItem source) {
        HomePurchaseItemDO target = new HomePurchaseItemDO();
        target.setId(source.getId());
        target.setBigTypeCode(source.getBigTypeCode());
        target.setBigTypeName(source.getBigTypeName());
        target.setTypeCode(source.getTypeCode());
        target.setTypeName(source.getTypeName());
        target.setProductName(source.getProductName());
        target.setQuantity(source.getQuantity());
        target.setBudgetText(source.getBudgetText());
        target.setBudgetMin(source.getBudgetMin());
        target.setBudgetMax(source.getBudgetMax());
        target.setInstallFee(source.getInstallFee());
        target.setRemark(source.getRemark());
        target.setUserId(source.getUserId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 家庭装修采购项查询参数；为空返回空对象
     * @return 家庭装修采购项查询参数（common-dal）
     */
    public static HomePurchaseItemDalQuery toDalQuery(HomePurchaseItemQueryParam source) {
        HomePurchaseItemDalQuery target = new HomePurchaseItemDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setBigTypeCode(source.getBigTypeCode());
        target.setBigTypeName(source.getBigTypeName());
        target.setTypeCode(source.getTypeCode());
        target.setTypeName(source.getTypeName());
        target.setProductName(source.getProductName());
        target.setQuantity(source.getQuantity());
        target.setBudgetText(source.getBudgetText());
        target.setBudgetMin(source.getBudgetMin());
        target.setBudgetMax(source.getBudgetMax());
        target.setInstallFee(source.getInstallFee());
        target.setRemark(source.getRemark());
        target.setUserId(source.getUserId());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
