package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jakt.aiplatform.biz.service.ClusterDomainManager;
import com.jakt.aiplatform.biz.service.ClusterDomainView;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.integration.ssh.SshClient;
import com.jakt.aiplatform.common.integration.ssh.SshResult;
import com.jakt.aiplatform.core.service.ClusterCiProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 域名映射用例编排：通过 SSH 调 cluster-ci 的 caddy_mapping.sh（Ingress + 公网 Caddy 双写）。
 */
@Service
public class ClusterDomainManagerImpl implements ClusterDomainManager {

    /** 域名映射脚本（cluster-ci/bin 下，随应用启动同步到 master）。 */
    private static final String SCRIPT = "caddy_mapping.sh";

    /** SSH 客户端。 */
    private final SshClient sshClient;

    /** cluster-ci 配置（master 主机/工作目录）。 */
    private final ClusterCiProperties ciProperties;

    public ClusterDomainManagerImpl(SshClient sshClient, ClusterCiProperties ciProperties) {
        this.sshClient = sshClient;
        this.ciProperties = ciProperties;
    }

    @Override
    public List<ClusterDomainView> list() {
        SshResult result = sshClient.execute(ciProperties.getMasterHost(),
                "bash " + scriptPath() + " list", 60);
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "读取域名映射失败: " + StrUtil.maxLength(result.getOutput(), 200));
        }
        JSONArray array = JSONUtil.parseArray(extractJson(result.getOutput()));
        List<ClusterDomainView> views = new ArrayList<>();
        for (Object item : array) {
            JSONObject obj = (JSONObject) item;
            ClusterDomainView view = new ClusterDomainView();
            view.setDomain(obj.getStr("domain"));
            view.setCaddy(obj.getBool("caddy"));
            view.setPrimary(obj.getBool("primary"));
            view.setType(obj.getStr("type"));
            view.setUpstream(obj.getStr("upstream"));
            view.setNamespace(obj.getStr("namespace"));
            view.setService(obj.getStr("service"));
            view.setPort(obj.getStr("port"));
            views.add(view);
        }
        return views;
    }

    @Override
    public void enable(String domain, String upstream) {
        StringBuilder command = new StringBuilder("bash ").append(scriptPath())
                .append(" enable '").append(domain).append("'");
        if (StrUtil.isNotBlank(upstream)) {
            command.append(" '").append(upstream).append("'");
        }
        SshResult result = sshClient.execute(ciProperties.getMasterHost(), command.toString(), 120);
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "开启公网映射失败: " + StrUtil.maxLength(result.getOutput(), 300));
        }
    }

    @Override
    public void disable(String domain) {
        SshResult result = sshClient.execute(ciProperties.getMasterHost(),
                "bash " + scriptPath() + " disable '" + domain + "'", 120);
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "关闭公网映射失败: " + StrUtil.maxLength(result.getOutput(), 300));
        }
    }

    /**
     * 脚本绝对路径。
     *
     * @return master 上的脚本路径
     */
    private String scriptPath() {
        return ciProperties.getWorkDir() + "/bin/" + SCRIPT;
    }

    /**
     * 从 SSH 输出里截取 JSON 数组（stdout/stderr 合并输出，可能带杂项文本）。
     *
     * @param output SSH 输出
     * @return JSON 数组字符串
     */
    private String extractJson(String output) {
        int start = output.indexOf('[');
        int end = output.lastIndexOf(']');
        if (start < 0 || end <= start) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "域名映射脚本返回结果异常");
        }
        return output.substring(start, end + 1);
    }
}
