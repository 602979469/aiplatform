package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import lombok.Getter;

/**
 * 镜像来源类型：自研(BUILD) / 现成(EXTERNAL)。
 */
@Getter
public enum ClusterImageTypeEnum implements BaseEnum<String> {

    /** 自研：git 地址 + Dockerfile 构建。 */
    BUILD("BUILD", "自研"),

    /** 现成：直接提供外部镜像地址导入。 */
    EXTERNAL("EXTERNAL", "现成"),
    ;

    private final String code;

    private final String desc;

    ClusterImageTypeEnum(String code, String desc) {
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
    public static ClusterImageTypeEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(ClusterImageTypeEnum.class, code);
    }
}
