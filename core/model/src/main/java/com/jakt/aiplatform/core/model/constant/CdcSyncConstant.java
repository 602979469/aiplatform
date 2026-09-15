package com.jakt.aiplatform.core.model.constant;

/**
 * ES 同步（CDC）常量：canal 的命名空间、资源名、映射 yml 的固定字段与任务约定。
 */
public final class CdcSyncConstant {

    /** canal 所在命名空间。 */
    public static final String NAMESPACE = "cdc";

    /** 映射配置 ConfigMap：一个 key 一条映射，key 即任务名。 */
    public static final String MAPPING_CONFIG_MAP = "canal-adapter-es8-mapping";

    /** canal-adapter Deployment（配置变更后需滚动重启才会生效）。 */
    public static final String ADAPTER_DEPLOYMENT = "canal-adapter";

    /** ES 适配器名（触发全量导入的 URL 段）。 */
    public static final String ADAPTER_NAME = "es8";

    /** 任务名后缀。 */
    public static final String TASK_SUFFIX = ".yml";

    /** 默认 destination（对应 canal-server 的 example 实例）。 */
    public static final String DEFAULT_DESTINATION = "example";

    /** 默认 groupId。 */
    public static final String DEFAULT_GROUP_ID = "g1";

    /** 默认数据源 key。 */
    public static final String DEFAULT_DATA_SOURCE = "defaultDS";

    /** 默认主键字段。 */
    public static final String DEFAULT_PK = "id";

    /** 默认批量提交条数。 */
    public static final int DEFAULT_COMMIT_BATCH = 3000;

    /** 补别名时使用的表别名（canal 增量 UPDATE 依赖列限定名，缺省会空指针）。 */
    public static final String TABLE_ALIAS = "a";

    /** 仅在 aiplatform 库内同步（canal-server 的 filter.regex 约定）。 */
    public static final String SYNC_DATABASE = "aiplatform";

    private CdcSyncConstant() {
    }
}
