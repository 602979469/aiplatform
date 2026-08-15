package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 镜像下载任务状态枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MirrorDownloadStatusEnum implements BaseEnum<String> {

    /** 已就绪（打包完成或本地已存在，可下载）。 */
    READY("ready", "可下载"),
    /** 生成中（拉取/打包）。 */
    GENERATING("generating", "生成中"),
    /** 失败。 */
    FAILED("failed", "失败"),
    ;

    /** code（对外状态值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    MirrorDownloadStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
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
     * @param code code
     * @return 枚举
     */
    @JsonCreator
    public static MirrorDownloadStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(MirrorDownloadStatusEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static MirrorDownloadStatusEnum fromCode(String code) {
        return BaseEnum.fromCode(MirrorDownloadStatusEnum.class, code);
    }
}
