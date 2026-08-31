package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import lombok.Getter;

/**
 * 镜像状态机：草稿 / 构建中 / 构建失败 / 已发布。
 */
@Getter
public enum ClusterImageStatusEnum implements BaseEnum<String> {

    /** 草稿：创建后初始状态，可编辑/删除/构建。 */
    DRAFT("DRAFT", "草稿"),

    /** 构建中：构建任务执行中，仅可查看。 */
    BUILDING("BUILDING", "构建中"),

    /** 构建失败：与草稿同权，可编辑/删除，可重新构建。 */
    BUILD_FAILED("BUILD_FAILED", "构建失败"),

    /** 已发布：构建成功，不可修改，可被 pod 配置绑定；可物理删除。 */
    PUBLISHED("PUBLISHED", "已发布"),
    ;

    private final String code;

    private final String desc;

    ClusterImageStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }

    @JsonCreator
    public static ClusterImageStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(ClusterImageStatusEnum.class, code);
    }
}
