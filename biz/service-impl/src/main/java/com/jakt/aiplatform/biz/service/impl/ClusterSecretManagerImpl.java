package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jakt.aiplatform.biz.service.ClusterSecretManager;
import com.jakt.aiplatform.biz.service.ClusterSecretView;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.ssh.SshClient;
import com.jakt.aiplatform.common.integration.ssh.SshResult;
import com.jakt.aiplatform.core.model.enums.BizNamespaceEnum;
import com.jakt.aiplatform.core.service.ClusterCiProperties;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 集群密钥管理实现：kubectl 走 master SSH，只取键名、值单向写入，永不回显。
 */
@Service
public class ClusterSecretManagerImpl implements ClusterSecretManager {

    /** 平台纳管标签。 */
    private static final String MANAGED_LABEL = "aiplatform-managed";

    /** 命名空间环境变量（与业务 pod 一致的白名单）。 */
    private static final String NAMESPACES_ENV = "AIPLATFORM_BIZ_NAMESPACES";

    private static final String DEFAULT_TYPE = "Opaque";

    /** 默认命名空间。 */
    private static final String DEFAULT_NAMESPACE = "tsk";

    private final SshClient sshClient;

    private final ClusterCiProperties ciProperties;

    public ClusterSecretManagerImpl(SshClient sshClient, ClusterCiProperties ciProperties) {
        this.sshClient = sshClient;
        this.ciProperties = ciProperties;
    }

    @Override
    public PageResult<ClusterSecretView> page(String namespace, String keyword, int pageNum, int pageSize) {
        String ns = StrUtil.blankToDefault(namespace, DEFAULT_NAMESPACE);
        checkNamespace(ns);
        List<ClusterSecretView> all = listFromCluster(ns);
        if (StrUtil.isNotBlank(keyword)) {
            String kw = keyword.trim().toLowerCase(Locale.ROOT);
            all = all.stream().filter(v -> v.getName().toLowerCase(Locale.ROOT).contains(kw)).toList();
        }
        int total = all.size();
        int from = Math.max(0, (pageNum - 1) * pageSize);
        int to = Math.min(total, from + pageSize);
        List<ClusterSecretView> dataList = from >= total ? List.of() : new ArrayList<>(all.subList(from, to));
        return new PageResult<>(total, pageNum, pageSize, dataList);
    }

    @Override
    public List<ClusterSecretView> options(String namespace) {
        String ns = StrUtil.blankToDefault(namespace, DEFAULT_NAMESPACE);
        checkNamespace(ns);
        return listFromCluster(ns).stream().filter(ClusterSecretView::isManaged).toList();
    }

    @Override
    public ClusterSecretView detail(String namespace, String name) {
        checkNamespace(namespace);
        String cmd = "kubectl get secret '" + name + "' -n '" + namespace + "' -o json";
        SshResult result = sshClient.execute(ciProperties.getMasterHost(), cmd, 30);
        AssertUtil.throwErrWhenFalse(result.isSuccess(), ErrorCodeEnum.SYSTEM_ERROR,
                "查询密钥失败: " + safeOutput(result.getOutput()));
        return parseFromJson(JSONUtil.parseObj(result.getOutput()), namespace, name);
    }

    @Override
    public void upsert(String namespace, String name, String type, Boolean exists,
                       Map<String, String> keyValues) {
        checkNamespace(namespace);
        if (keyValues == null || keyValues.isEmpty()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "至少提交一个键值");
        }
        // 1. 服务端读取当前 Secret（kubectl -o yaml，SnakeYAML 解析；不存在则新建）
        String readCmd = "kubectl get secret '" + name + "' -n '" + namespace + "' -o yaml";
        SshResult read = sshClient.execute(ciProperties.getMasterHost(), readCmd, 30);
        boolean knownExists = read.isSuccess() && StrUtil.isNotBlank(read.getOutput());
        if (!knownExists && !read.getOutput().contains("NotFound")) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "读取密钥失败: " + safeOutput(read.getOutput()));
        }
        if (Boolean.FALSE.equals(exists) && knownExists) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID,
                    "已存在同名 Secret，如需修改请使用编辑操作: " + name);
        }
        if (Boolean.TRUE.equals(exists) && !knownExists) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID,
                    "Secret 不存在或已被删除: " + name);
        }

        Map<String, Object> root;
        if (knownExists) {
            Object loaded = new Yaml().load(read.getOutput());
            if (!(loaded instanceof Map)) {
                throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "读取密钥失败: 内容格式异常");
            }
            root = castMap(loaded);
        } else {
            root = new LinkedHashMap<>();
            root.put("apiVersion", "v1");
            root.put("kind", "Secret");
            root.put("metadata", new LinkedHashMap<String, Object>());
        }

        Map<String, Object> meta = castMap(root.computeIfAbsent("metadata", k -> new LinkedHashMap<String, Object>()));
        meta.putIfAbsent("name", name);
        meta.putIfAbsent("namespace", namespace);
        meta.putIfAbsent("labels", new LinkedHashMap<String, Object>());
        Map<String, Object> labels = castMap(meta.get("labels"));
        labels.put(MANAGED_LABEL, "true");

        String currentType = root.get("type") == null ? null : String.valueOf(root.get("type"));
        String actualType = StrUtil.blankToDefault(StrUtil.blankToDefault(type, currentType), DEFAULT_TYPE);
        root.put("type", actualType);

        Map<String, Object> data = root.get("data") == null ? new LinkedHashMap<>() : castMap(root.get("data"));
        root.put("data", data);

        // 2. 合并：仅覆盖/新增提交的键（不删除），值 base64 后写入
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            data.put(entry.getKey(),
                    Base64.getEncoder().encodeToString(entry.getValue().getBytes(StandardCharsets.UTF_8)));
        }

        // 3. 清理服务端只读字段，避免 apply 冲突
        root.remove("status");
        meta.remove("creationTimestamp");
        meta.remove("uid");
        meta.remove("resourceVersion");
        meta.remove("generation");
        meta.remove("managedFields");
        meta.remove("selfLink");
        meta.remove("deletionTimestamp");
        meta.remove("deletionGracePeriodSeconds");

        // 4. SnakeYAML 回写 → 上传临时文件 → kubectl apply（值不进命令行/ps）
        String yaml = dumpYaml(root);
        String remoteFile = "/tmp/secret-manage-" + System.currentTimeMillis()
                + "-" + ThreadLocalRandom.current().nextInt(10000) + ".yaml";
        Path local = null;
        try {
            local = Files.createTempFile("secret-manage-", ".yaml");
            Files.writeString(local, yaml, StandardCharsets.UTF_8);
            sshClient.uploadFile(ciProperties.getMasterHost(), local.toString(), remoteFile);
            SshResult apply = sshClient.execute(ciProperties.getMasterHost(),
                    "chmod 600 '" + remoteFile + "' && kubectl apply -f '" + remoteFile
                            + "'; rc=$?; rm -f '" + remoteFile + "'; exit $rc",
                    60);
            AssertUtil.throwErrWhenFalse(apply.isSuccess(), ErrorCodeEnum.SYSTEM_ERROR,
                    "同步密钥失败: " + safeOutput(apply.getOutput()));
        } catch (Exception e) {
            sshClient.execute(ciProperties.getMasterHost(), "rm -f '" + remoteFile + "'", 20);
            if (e instanceof AiPlatformException) {
                throw (AiPlatformException) e;
            }
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "同步密钥失败: " + e.getMessage());
        } finally {
            if (local != null) {
                try {
                    Files.deleteIfExists(local);
                } catch (Exception ignored) {
                    // 临时文件清理失败不影响主流程
                }
            }
        }
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "集群密钥已同步 namespace={} name={} type={} keys={}",
                namespace, name, actualType, new ArrayList<>(keyValues.keySet()));
    }

    private List<ClusterSecretView> listFromCluster(String namespace) {
        String cmd = "kubectl get secret -n '" + namespace + "' -o json";
        SshResult result = sshClient.execute(ciProperties.getMasterHost(), cmd, 30);
        AssertUtil.throwErrWhenFalse(result.isSuccess(), ErrorCodeEnum.SYSTEM_ERROR,
                "查询密钥列表失败: " + safeOutput(result.getOutput()));
        List<ClusterSecretView> views = new ArrayList<>();
        JSONObject root = JSONUtil.parseObj(result.getOutput());
        JSONArray items = root.getJSONArray("items");
        if (items != null) {
            for (Object item : items) {
                JSONObject obj = (JSONObject) item;
                String name = StrUtil.blankToDefault(
                        obj.getJSONObject("metadata") == null ? null
                                : obj.getJSONObject("metadata").getStr("name"), "");
                if (StrUtil.isBlank(name)) {
                    continue;
                }
                views.add(parseFromJson(obj, namespace, name));
            }
        }
        views.sort((a, b) -> a.getName().compareTo(b.getName()));
        return views;
    }

    private ClusterSecretView parseFromJson(JSONObject json, String namespace, String name) {
        ClusterSecretView view = new ClusterSecretView();
        view.setNamespace(namespace);
        view.setName(name);
        view.setType(StrUtil.blankToDefault(json.getStr("type"), DEFAULT_TYPE));
        JSONObject meta = json.getJSONObject("metadata");
        JSONObject labels = meta == null ? null : meta.getJSONObject("labels");
        view.setManaged(labels != null && "true".equals(labels.getStr(MANAGED_LABEL)));
        Set<String> keys = new TreeSet<>();
        JSONObject data = json.getJSONObject("data");
        if (data != null) {
            for (String key : data.keySet()) {
                keys.add(key);
            }
        }
        view.setKeys(new ArrayList<>(keys));
        return view;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }

    private String dumpYaml(Map<String, Object> root) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        options.setPrettyFlow(true);
        return new Yaml(options).dump(root);
    }

    private void checkNamespace(String namespace) {
        AssertUtil.throwErrWhenBlank(namespace, ErrorCodeEnum.PARAM_INVALID, "命名空间不能为空");
        AssertUtil.throwErrWhenFalse(allowedNamespaces().contains(namespace), ErrorCodeEnum.PARAM_INVALID,
                "命名空间不在允许范围: " + namespace);
    }

    private List<String> allowedNamespaces() {
        String envNamespaces = System.getenv(NAMESPACES_ENV);
        if (StrUtil.isNotBlank(envNamespaces)) {
            List<String> list = new ArrayList<>();
            for (String ns : envNamespaces.split(",")) {
                if (StrUtil.isNotBlank(ns.trim())) {
                    list.add(ns.trim());
                }
            }
            return list;
        }
        List<String> defaults = new ArrayList<>();
        for (BizNamespaceEnum ns : BizNamespaceEnum.values()) {
            defaults.add(ns.getCode());
        }
        return defaults;
    }

    /** kubectl 错误输出脱敏：只取前 200 字符，避免任何值卷入日志。 */
    private String safeOutput(String output) {
        if (StrUtil.isBlank(output)) {
            return "";
        }
        String trimmed = output.trim();
        return trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
    }
}
