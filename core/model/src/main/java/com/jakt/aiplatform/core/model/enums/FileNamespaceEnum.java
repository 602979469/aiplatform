package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import lombok.Getter;

/**
 * 文件管理业务命名空间枚举：系统允许文件上传/查询的目标命名空间。
 *
 * <p>优先读环境变量 AIPLATFORM_FILE_NAMESPACES（逗号分隔），未配置时用本枚举默认值。
 */
@Getter
public enum FileNamespaceEnum implements BaseEnum<String> {

    /** aiplatform 命名空间。 */
    AI_PLATFORM("aiplatform", "aiplatform 命名空间"),

    /** jianli 命名空间。 */
    JIANLI("jianli", "jianli 命名空间"),

    /** 用户头像命名空间。 */
    USER_AVATAR("user_avatar", "用户头像命名空间"),
    ;

    /** code（命名空间名）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    FileNamespaceEnum(String code, String desc) {
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
     * @param code 命名空间名
     * @return 枚举；未匹配抛 ENUM_NOT_MATCHED
     */
    @JsonCreator
    public static FileNamespaceEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(FileNamespaceEnum.class, code);
    }
}
