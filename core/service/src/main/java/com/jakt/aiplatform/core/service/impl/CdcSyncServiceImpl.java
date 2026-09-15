package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.canal.CanalAdapterClient;
import com.jakt.aiplatform.common.integration.elastic.EsAdminClient;
import com.jakt.aiplatform.common.integration.k8s.K8sClient;
import com.jakt.aiplatform.common.integration.k8s.K8sDeploymentInfo;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.core.model.constant.CdcSyncConstant;
import com.jakt.aiplatform.core.model.domain.CdcEsMapping;
import com.jakt.aiplatform.core.model.domain.CdcMappingCheck;
import com.jakt.aiplatform.core.model.domain.CdcMappingPreview;
import com.jakt.aiplatform.core.model.domain.CdcSyncStatus;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.service.CdcSyncService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ES 同步（canal CDC）配置实现。
 *
 * <p>配置形态：ConfigMap {@code cdc/canal-adapter-es8-mapping} 的每个 key 是一个映射任务 yml，
 * key 名（含 .yml）同时是全量导入接口的 URL 参数。写入后必须滚动重启 canal-adapter 才生效。
 */
@Service
public class CdcSyncServiceImpl implements CdcSyncService {

    /** 校验项级别：阻断保存。 */
    private static final String LEVEL_ERROR = "ERROR";

    /** 校验项级别：提醒，不阻断。 */
    private static final String LEVEL_WARN = "WARN";

    /** 校验项级别：说明。 */
    private static final String LEVEL_INFO = "INFO";

    /** 任务名规则（会成为 ConfigMap key、文件名与 URL 参数）。 */
    private static final String NAME_PATTERN = "^[a-z0-9_]{2,64}$";

    /** ES 索引名规则。 */
    private static final String INDEX_PATTERN = "^[a-z0-9][a-z0-9._-]*$";

    /** from 子句：表名 + 可选别名。 */
    private static final Pattern FROM_PATTERN = Pattern.compile(
            "(?i)\\bfrom\\s+([A-Za-z_][A-Za-z0-9_$]*)(?:\\s+(?:as\\s+)?([A-Za-z_][A-Za-z0-9_$]*))?");

    /** from 之后的关键字：出现在这里说明没有表别名。 */
    private static final Set<String> SQL_KEYWORDS = Set.of(
            "where", "group", "order", "limit", "join", "left", "right", "inner", "outer", "on", "having", "union");

    /** Kubernetes 客户端。 */
    private final K8sClient k8sClient;

    /** canal-adapter 管理接口客户端。 */
    private final CanalAdapterClient canalAdapterClient;

    /** Elasticsearch 管理查询客户端。 */
    private final EsAdminClient esAdminClient;

    public CdcSyncServiceImpl(K8sClient k8sClient, CanalAdapterClient canalAdapterClient, EsAdminClient esAdminClient) {
        this.k8sClient = k8sClient;
        this.canalAdapterClient = canalAdapterClient;
        this.esAdminClient = esAdminClient;
    }

    @Override
    public List<CdcEsMapping> listMappings() {
        Map<String, String> data = k8sClient.getConfigMapData(CdcSyncConstant.NAMESPACE,
                CdcSyncConstant.MAPPING_CONFIG_MAP);
        List<String> keys = new ArrayList<>(data.keySet());
        keys.sort(String::compareTo);
        List<CdcEsMapping> mappings = new ArrayList<>();
        for (String key : keys) {
            String yml = data.get(key);
            mappings.add(parseYml(key, yml));
        }
        return mappings;
    }

    @Override
    public CdcEsMapping getMapping(String name) {
        String key = configMapKey(normalizeName(name));
        Map<String, String> data = k8sClient.getConfigMapData(CdcSyncConstant.NAMESPACE,
                CdcSyncConstant.MAPPING_CONFIG_MAP);
        String yml = data.get(key);
        if (StrUtil.isBlank(yml)) {
            return null;
        }
        return parseYml(key, yml);
    }

    @Override
    public CdcMappingPreview preview(CdcEsMapping mapping) {
        List<CdcMappingCheck> checks = new ArrayList<>();
        String name = normalizeName(mapping.getName());
        boolean nameOk = StrUtil.isNotBlank(name) && name.matches(NAME_PATTERN);
        addCheck(checks, "任务名", LEVEL_ERROR, nameOk, nameOk
                ? "任务名 " + name + " 合法（会成为 ConfigMap 的 key 与全量导入的任务名）"
                : "任务名只能是 2-64 位小写字母/数字/下划线，例如 kb_question");

        String index = StrUtil.trimToEmpty(mapping.getEsIndex());
        boolean indexOk = index.matches(INDEX_PATTERN);
        addCheck(checks, "索引名", LEVEL_ERROR, indexOk, indexOk
                ? "索引名 " + index + " 合法"
                : "ES 索引名只能小写，且不能以 . _ - 开头");
        if (indexOk) {
            checkIndexExists(checks, index);
        }

        String sql = toOneLine(mapping.getSql());
        boolean sqlOk = StrUtil.isNotBlank(sql) && StrUtil.startWithIgnoreCase(sql, "select");
        addCheck(checks, "SQL 结构", LEVEL_ERROR, sqlOk, sqlOk
                ? "SQL 以 select 开头"
                : "SQL 必须是以 select 开头的单表查询，例如 select a.id as id from kb_question a");

        String table = sqlTableName(sql);
        boolean tableOk = StrUtil.isNotBlank(table);
        addCheck(checks, "来源表", LEVEL_ERROR, tableOk, tableOk
                ? "来源表 " + table + "（canal 只同步 " + CdcSyncConstant.SYNC_DATABASE + " 库，换库需改 canal-server 配置）"
                : "无法从 SQL 中解析出表名，请检查 from 子句");

        checkColumnQualifier(checks, sql);

        String pk = StrUtil.trimToEmpty(mapping.getPk());
        addCheck(checks, "主键字段", LEVEL_ERROR, StrUtil.isNotBlank(pk), StrUtil.isNotBlank(pk)
                ? "主键 " + pk + "（决定 ES 文档 _id）"
                : "必须指定主键字段，通常就是 id");

        checkDestination(checks, mapping);

        addCheck(checks, "同步范围", LEVEL_INFO, true,
                "canal-server 当前的过滤规则是 " + CdcSyncConstant.SYNC_DATABASE
                        + " 库全库：同库加表只改映射即可，跨库要改 canal-server 配置并重启它");

        CdcMappingPreview preview = new CdcMappingPreview();
        preview.setChecks(checks);
        preview.setPass(isAllPassed(checks));
        preview.setYml(buildYml(mapping));
        return preview;
    }

    @Override
    public void saveMapping(CdcEsMapping mapping, Boolean restart) {
        CdcMappingPreview preview = preview(mapping);
        if (!Boolean.TRUE.equals(preview.getPass())) {
            throw AiPlatformException.ofThrow(CommonErrorCode.PARAM_INVALID, firstErrorMessage(preview.getChecks()));
        }
        String name = normalizeName(mapping.getName());
        Map<String, String> data = k8sClient.getConfigMapData(CdcSyncConstant.NAMESPACE,
                CdcSyncConstant.MAPPING_CONFIG_MAP);
        data.put(configMapKey(name), preview.getYml());
        k8sClient.applyConfigMap(CdcSyncConstant.NAMESPACE, CdcSyncConstant.MAPPING_CONFIG_MAP, data);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【CDC】保存映射 task={} index={}", name, mapping.getEsIndex());
        if (!Boolean.FALSE.equals(restart)) {
            restartAdapter();
        }
    }

    @Override
    public void deleteMapping(String name, Boolean restart) {
        String key = configMapKey(normalizeName(name));
        Map<String, String> data = k8sClient.getConfigMapData(CdcSyncConstant.NAMESPACE,
                CdcSyncConstant.MAPPING_CONFIG_MAP);
        if (StrUtil.isBlank(data.remove(key))) {
            throw AiPlatformException.ofThrow(BizErrorCodeEnum.RESOURCE_NOT_FOUND, "映射不存在: " + key);
        }
        k8sClient.applyConfigMap(CdcSyncConstant.NAMESPACE, CdcSyncConstant.MAPPING_CONFIG_MAP, data);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【CDC】删除映射 task={}", key);
        if (!Boolean.FALSE.equals(restart)) {
            restartAdapter();
        }
    }

    @Override
    public String formatSql(String sql) {
        String normalized = toOneLine(sql);
        String table = sqlTableName(normalized);
        if (StrUtil.isBlank(table)) {
            throw AiPlatformException.ofThrow(CommonErrorCode.PARAM_INVALID, "无法解析表名，请检查 from 子句");
        }
        String alias = StrUtil.blankToDefault(sqlTableAlias(normalized), CdcSyncConstant.TABLE_ALIAS);
        List<String> columns = sqlColumns(normalized);
        List<String> formatted = new ArrayList<>();
        for (String column : columns) {
            formatted.add(qualifyColumn(column, alias));
        }
        StringBuilder builder = new StringBuilder("select ");
        builder.append(String.join(",\n       ", formatted));
        builder.append("\n  from ").append(table).append(' ').append(alias);
        Matcher matcher = FROM_PATTERN.matcher(normalized);
        if (matcher.find()) {
            String tail = StrUtil.trim(normalized.substring(matcher.end()));
            if (StrUtil.isNotBlank(tail)) {
                builder.append(' ').append(tail);
            }
        }
        return builder.toString();
    }

    @Override
    public String triggerEtl(String name) {
        CdcEsMapping mapping = getMapping(name);
        if (mapping == null) {
            throw AiPlatformException.ofThrow(BizErrorCodeEnum.RESOURCE_NOT_FOUND, "映射不存在: " + name);
        }
        String task = configMapKey(normalizeName(name));
        String response = canalAdapterClient.triggerEtl(CdcSyncConstant.ADAPTER_NAME, task);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【CDC】触发全量导入 task={} resp={}", task, response);
        return response;
    }

    @Override
    public void restartAdapter() {
        k8sClient.restartDeployment(CdcSyncConstant.NAMESPACE, CdcSyncConstant.ADAPTER_DEPLOYMENT);
    }

    @Override
    public CdcSyncStatus status() {
        CdcSyncStatus status = new CdcSyncStatus();
        K8sDeploymentInfo deployment = k8sClient.getDeployment(CdcSyncConstant.NAMESPACE,
                CdcSyncConstant.ADAPTER_DEPLOYMENT);
        if (deployment != null) {
            status.setReplicas(deployment.getDesiredReplicas());
            status.setReadyReplicas(deployment.getReadyReplicas());
        }
        try {
            status.setDestinations(canalAdapterClient.destinations());
        } catch (CommonException e) {
            LoggerUtil.warn(LogFileEnum.COMMON_ERROR, "【CDC】查询 canal 实例状态失败: {}", e.getErrorMessage());
            status.setDestinations("调用失败: " + e.getErrorMessage());
        }
        return status;
    }

    @Override
    public List<String> listEsIndices() {
        return esAdminClient.listIndices();
    }

    /**
     * 校验目标索引是否存在（canal 不会自动建索引）。
     *
     * @param checks 校验项列表
     * @param index  索引名
     */
    private void checkIndexExists(List<CdcMappingCheck> checks, String index) {
        try {
            boolean exists = esAdminClient.indexExists(index);
            addCheck(checks, "索引已存在", LEVEL_ERROR, exists, exists
                    ? "ES 索引 " + index + " 已存在"
                    : "ES 索引 " + index + " 不存在：canal 不会自动建索引，请先创建索引和字段类型再保存");
        } catch (CommonException e) {
            LoggerUtil.warn(LogFileEnum.COMMON_ERROR, "【CDC】索引校验失败 index={} err={}", index, e.getErrorMessage());
            addCheck(checks, "索引已存在", LEVEL_WARN, true,
                    "无法连接 Elasticsearch 校验索引（" + e.getErrorMessage() + "），请自行确认索引已创建");
        }
    }

    /**
     * 校验 SQL 的表别名与列限定名（裸列名会让 canal 增量 UPDATE 报空指针）。
     *
     * @param checks 校验项列表
     * @param sql    单行 SQL
     */
    private void checkColumnQualifier(List<CdcMappingCheck> checks, String sql) {
        String alias = sqlTableAlias(sql);
        boolean aliasOk = StrUtil.isNotBlank(alias);
        addCheck(checks, "表别名", LEVEL_ERROR, aliasOk, aliasOk
                ? "表别名 " + alias + " 已就位"
                : "SQL 必须给表起别名（如 from kb_question a）：无别名时 canal 增量 UPDATE 会抛空指针，"
                        + "该表后续所有变更都会卡在重试队列里");
        List<String> bareColumns = new ArrayList<>();
        List<String> columns = sqlColumns(sql);
        for (String column : columns) {
            if (!StrUtil.contains(column, ".") && !StrUtil.contains(column, "(")) {
                bareColumns.add(StrUtil.trim(column));
            }
        }
        boolean columnOk = CollUtil.isEmpty(bareColumns);
        addCheck(checks, "列限定名", LEVEL_ERROR, columnOk, columnOk
                ? "所有列都带表限定名"
                : "这些列没有表限定名：" + String.join(", ", bareColumns)
                        + "（点一下“格式化 SQL”会自动补成 别名.列名 as 列名）");
    }

    /**
     * 校验 destination / groupId 是否与 canal-server 的实例一致。
     *
     * @param checks  校验项列表
     * @param mapping 映射
     */
    private void checkDestination(List<CdcMappingCheck> checks, CdcEsMapping mapping) {
        String destination = StrUtil.blankToDefault(mapping.getDestination(), CdcSyncConstant.DEFAULT_DESTINATION);
        String groupId = StrUtil.blankToDefault(mapping.getGroupId(), CdcSyncConstant.DEFAULT_GROUP_ID);
        boolean matched = CdcSyncConstant.DEFAULT_DESTINATION.equals(destination)
                && CdcSyncConstant.DEFAULT_GROUP_ID.equals(groupId);
        addCheck(checks, "同步实例", LEVEL_WARN, matched, matched
                ? "destination=" + destination + " groupId=" + groupId + "，与 canal-server 的实例一致"
                : "destination=" + destination + " groupId=" + groupId + " 与 canal-server 当前实例（"
                        + CdcSyncConstant.DEFAULT_DESTINATION + "/" + CdcSyncConstant.DEFAULT_GROUP_ID
                        + "）不一致，除非确实新增了实例，否则请改回默认值");
    }

    /**
     * 追加一条校验结果。
     *
     * @param checks  校验项列表
     * @param item    校验项名称
     * @param level   级别（ERROR/WARN/INFO）
     * @param passed  是否通过
     * @param message 说明
     */
    private void addCheck(List<CdcMappingCheck> checks, String item, String level, boolean passed, String message) {
        CdcMappingCheck check = new CdcMappingCheck();
        check.setItem(item);
        check.setLevel(level);
        check.setPassed(passed);
        check.setMessage(message);
        checks.add(check);
    }

    /**
     * 是否没有阻断项。
     *
     * @param checks 校验项列表
     * @return true 全部通过
     */
    private boolean isAllPassed(List<CdcMappingCheck> checks) {
        for (CdcMappingCheck check : checks) {
            if (LEVEL_ERROR.equals(check.getLevel()) && !Boolean.TRUE.equals(check.getPassed())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 取第一条阻断项说明（保存失败提示用）。
     *
     * @param checks 校验项列表
     * @return 提示文本
     */
    private String firstErrorMessage(List<CdcMappingCheck> checks) {
        for (CdcMappingCheck check : checks) {
            if (LEVEL_ERROR.equals(check.getLevel()) && !Boolean.TRUE.equals(check.getPassed())) {
                return check.getItem() + "：" + check.getMessage();
            }
        }
        return "同步配置校验未通过";
    }

    /**
     * 解析映射 yml（结构固定，逐行取值，不引 YAML 依赖）。
     *
     * @param name 任务名（含 .yml）
     * @param yml  yml 文本
     * @return 映射
     */
    private CdcEsMapping parseYml(String name, String yml) {
        CdcEsMapping mapping = new CdcEsMapping();
        mapping.setName(normalizeName(name));
        mapping.setRawYml(yml);
        String content = StrUtil.nullToEmpty(yml);
        for (String line : content.split("\n")) {
            String trimmed = StrUtil.trim(line);
            if (StrUtil.isBlank(trimmed) || StrUtil.startWith(trimmed, "#")) {
                continue;
            }
            int separator = trimmed.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            String key = StrUtil.trim(trimmed.substring(0, separator));
            String value = unquote(StrUtil.trim(trimmed.substring(separator + 1)));
            if ("dataSourceKey".equals(key)) {
                mapping.setDataSourceKey(value);
            } else if ("destination".equals(key)) {
                mapping.setDestination(value);
            } else if ("groupId".equals(key)) {
                mapping.setGroupId(value);
            } else if ("_index".equals(key)) {
                mapping.setEsIndex(value);
            } else if ("_id".equals(key) || "pk".equals(key)) {
                mapping.setPk(value);
            } else if ("upsert".equals(key)) {
                mapping.setUpsert(Boolean.valueOf(value));
            } else if ("commitBatch".equals(key)) {
                mapping.setCommitBatch(NumberUtil.parseInt(value, CdcSyncConstant.DEFAULT_COMMIT_BATCH));
            } else if ("sql".equals(key)) {
                mapping.setSql(value);
            }
        }
        mapping.setTableName(sqlTableName(toOneLine(mapping.getSql())));
        return mapping;
    }

    /**
     * 生成映射 yml 文本。
     *
     * @param mapping 映射
     * @return yml 文本
     */
    private String buildYml(CdcEsMapping mapping) {
        String name = normalizeName(mapping.getName());
        String index = StrUtil.trimToEmpty(mapping.getEsIndex());
        String pk = StrUtil.blankToDefault(StrUtil.trimToEmpty(mapping.getPk()), CdcSyncConstant.DEFAULT_PK);
        String sql = toOneLine(mapping.getSql());
        int commitBatch = mapping.getCommitBatch() == null
                ? CdcSyncConstant.DEFAULT_COMMIT_BATCH : mapping.getCommitBatch();
        boolean upsert = !Boolean.FALSE.equals(mapping.getUpsert());
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(name).append("：把 ").append(sqlTableName(sql))
                .append(" 表同步到 ES 索引 ").append(index).append('\n');
        builder.append("# 注意：SQL 必须带表别名并对每列做限定（a.xxx as xxx），否则 canal 增量 UPDATE 会空指针\n");
        builder.append("dataSourceKey: ")
                .append(StrUtil.blankToDefault(mapping.getDataSourceKey(), CdcSyncConstant.DEFAULT_DATA_SOURCE))
                .append('\n');
        builder.append("destination: ")
                .append(StrUtil.blankToDefault(mapping.getDestination(), CdcSyncConstant.DEFAULT_DESTINATION))
                .append('\n');
        builder.append("groupId: ")
                .append(StrUtil.blankToDefault(mapping.getGroupId(), CdcSyncConstant.DEFAULT_GROUP_ID))
                .append('\n');
        builder.append("esMapping:\n");
        builder.append("  _index: ").append(index).append('\n');
        builder.append("  _id: ").append(pk).append('\n');
        builder.append("  upsert: ").append(upsert).append('\n');
        builder.append("  pk: ").append(pk).append('\n');
        builder.append("  sql: \"").append(sql).append("\"\n");
        builder.append("  commitBatch: ").append(commitBatch).append('\n');
        return builder.toString();
    }

    /**
     * 归一化任务名（去掉 .yml 后缀）。
     *
     * @param name 原始任务名
     * @return 归一化任务名
     */
    private String normalizeName(String name) {
        String trimmed = StrUtil.trimToEmpty(name);
        return StrUtil.endWithIgnoreCase(trimmed, CdcSyncConstant.TASK_SUFFIX)
                ? StrUtil.removeSuffixIgnoreCase(trimmed, CdcSyncConstant.TASK_SUFFIX) : trimmed;
    }

    /**
     * ConfigMap key（任务名 + .yml）。
     *
     * @param name 归一化任务名
     * @return ConfigMap key
     */
    private String configMapKey(String name) {
        return name + CdcSyncConstant.TASK_SUFFIX;
    }

    /**
     * 去掉包裹的双引号。
     *
     * @param value 原始值
     * @return 去引号后的值
     */
    private String unquote(String value) {
        return StrUtil.strip(value, "\"", "\"");
    }

    /**
     * 压成单行并收敛空白（yml 里 sql 必须是单行）。
     *
     * @param sql 原始 SQL
     * @return 单行 SQL
     */
    private String toOneLine(String sql) {
        return StrUtil.trim(StrUtil.nullToEmpty(sql).replaceAll("\\s+", " "));
    }

    /**
     * 解析 SQL 里的表名。
     *
     * @param sql 单行 SQL
     * @return 表名；解析不到返回空串
     */
    private String sqlTableName(String sql) {
        Matcher matcher = FROM_PATTERN.matcher(StrUtil.nullToEmpty(sql));
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * 解析 SQL 里的表别名。
     *
     * @param sql 单行 SQL
     * @return 表别名；没有则返回 null
     */
    private String sqlTableAlias(String sql) {
        Matcher matcher = FROM_PATTERN.matcher(StrUtil.nullToEmpty(sql));
        if (!matcher.find()) {
            return null;
        }
        String alias = matcher.group(2);
        if (StrUtil.isBlank(alias) || SQL_KEYWORDS.contains(alias.toLowerCase())) {
            return null;
        }
        return alias;
    }

    /**
     * 拆出 select 与 from 之间的列表达式。
     *
     * @param sql 单行 SQL
     * @return 列表达式列表
     */
    private List<String> sqlColumns(String sql) {
        String content = StrUtil.nullToEmpty(sql);
        Matcher matcher = FROM_PATTERN.matcher(content);
        int selectIndex = StrUtil.indexOfIgnoreCase(content, "select");
        if (selectIndex < 0 || !matcher.find()) {
            return new ArrayList<>();
        }
        String body = content.substring(selectIndex + "select".length(), matcher.start());
        return splitTopLevel(body);
    }

    /**
     * 按顶层逗号切分（忽略括号内的逗号）。
     *
     * @param body 表达式文本
     * @return 切分结果
     */
    private List<String> splitTopLevel(String body) {
        List<String> parts = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (char ch : body.toCharArray()) {
            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
            }
            if (ch == ',' && depth == 0) {
                parts.add(StrUtil.trim(current.toString()));
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        if (StrUtil.isNotBlank(current.toString())) {
            parts.add(StrUtil.trim(current.toString()));
        }
        return parts;
    }

    /**
     * 给单个列表达式补表别名与列别名。
     *
     * @param column 列表达式
     * @param alias  表别名
     * @return 形如 a.id as id 的列表达式
     */
    private String qualifyColumn(String column, String alias) {
        String expression = StrUtil.removeAll(StrUtil.trim(column), '`');
        String body = expression;
        String columnAlias = null;
        int asIndex = StrUtil.lastIndexOfIgnoreCase(expression, " as ");
        if (asIndex > 0) {
            body = StrUtil.trim(expression.substring(0, asIndex));
            columnAlias = StrUtil.trim(expression.substring(asIndex + 4));
        } else {
            int spaceIndex = expression.lastIndexOf(' ');
            if (spaceIndex > 0 && !StrUtil.contains(expression, "(")) {
                body = StrUtil.trim(expression.substring(0, spaceIndex));
                columnAlias = StrUtil.trim(expression.substring(spaceIndex + 1));
            }
        }
        if (StrUtil.contains(body, "(")) {
            return expression;
        }
        if (StrUtil.isBlank(columnAlias)) {
            columnAlias = StrUtil.contains(body, ".") ? StrUtil.subAfter(body, ".", true) : body;
        }
        String qualified = StrUtil.contains(body, ".") ? body : alias + "." + body;
        return qualified + " as " + columnAlias;
    }
}
