package com.jakt.aiplatform.core.model.constant;

/**
 * 家庭装修采购常量：场景码、能力编码、数量上限等语义值统一收口，禁止散落魔法值。
 */
public final class HomePurchaseConstant {

    /** AI 能力场景码（sys_ai_capability.scene_code）。 */
    public static final String SCENE_CODE = "HOME_PURCHASE";

    /** 产品推荐能力编码（sys_ai_capability.capability_code）。 */
    public static final String CAPABILITY_PRODUCT_RECOMMEND = "PRODUCT_RECOMMEND";

    /** 单个采购项最多图片数。 */
    public static final int MAX_IMAGE_COUNT = 10;

    /** 默认采购数量。 */
    public static final int DEFAULT_QUANTITY = 1;

    private HomePurchaseConstant() {
    }
}
