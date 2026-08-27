package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import lombok.Getter;

/**
 * 业务 pod 配置状态机：草稿 / 构建中 / 构建失败 / 发布 / 弃用。
 */
@Getter
public enum ClusterPodConfigStatusEnum implements BaseEnum<String> {

    /** 草稿：创建后初始状态，可编辑/删除/构建。 */
    DRAFT("DRAFT", "草稿"),

    /** 构建中：构建任务执行中，不可编辑/删除，可重试构建。 */
    BUILDING("BUILDING", "构建中"),

    /** 构建失败：与草稿同权，可编辑/删除，修改后可重新构建。 */
    BUILD_FAILED("BUILD_FAILED", "构建失败"),

    /** 发布：构建通过，不可编辑/删除，可再次部署，可弃用。 */
    PUBLISHED("PUBLISHED", "发布"),

    /** 弃用：不可编辑/删除/部署，只有查看。 */
    RETIRED("RETIRED", "弃用"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    ClusterPodConfigStatusEnum(String code, String desc) {
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

    /**
     * 按 code 反查枚举；Jackson 反序列化入口。
     *
     * @param code 状态码
     * @return 枚举
     */
    @JsonCreator
    public static ClusterPodConfigStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(ClusterPodConfigStatusEnum.class, code);
    }
}
