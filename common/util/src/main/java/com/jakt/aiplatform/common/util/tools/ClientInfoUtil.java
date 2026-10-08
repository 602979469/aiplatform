package com.jakt.aiplatform.common.util.tools;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.util.StrUtil;

/**
 * 客户端信息工具：统一从 Sa-Token 请求上下文获取 IP 与 User-Agent。
 */
public final class ClientInfoUtil {

    /** User-Agent 存储长度上限。 */
    private static final int USER_AGENT_MAX_LENGTH = 255;

    private ClientInfoUtil() {
    }

    /**
     * 获取客户端 IP。
     *
     * <p>读取顺序：X-Forwarded-For 最左侧（可信代理写入的真实客户端）→ X-Real-IP → 缺省 127.0.0.1。
     * 公网链路为 Caddy（frp 隧道）→ ingress-nginx：frp 会把连接源地址改成内网地址（如 10.244.0.1），
     * nginx 据此回填的 X-Real-IP 同样是内网地址，只有 Caddy 追加在 X-Forwarded-For 最左侧的才是真实客户端。
     *
     * @return 客户端 IP
     */
    public static String getClientIp() {
        // 优先取 X-Forwarded-For：公网链路是 Caddy（frp 隧道）→ ingress-nginx，
        // frp 会把连接源地址改成内网地址（如 10.244.0.1），nginx 据此回填的 X-Real-IP 也是内网地址，
        // 只有 Caddy 追加在 X-Forwarded-For 最左侧的才是真实客户端 IP。
        String ip = SaHolder.getRequest().getHeader("X-Forwarded-For");
        if (isInvalidIp(ip)) {
            ip = SaHolder.getRequest().getHeader("X-Real-IP");
        }
        if (isInvalidIp(ip)) {
            return "127.0.0.1";
        }
        // X-Forwarded-For 可能是逗号分隔的多级代理列表，取最左侧真实客户端 IP
        int commaIndex = ip.indexOf(',');
        if (commaIndex >= 0) {
            ip = ip.substring(0, commaIndex);
        }
        ip = ip.trim();
        // 代理以 IPv6 映射格式上报 IPv4（如 ::ffff:192.168.3.212），归一化为纯 IPv4
        if (ip.startsWith("::ffff:")) {
            ip = ip.substring("::ffff:".length());
        }
        return ip;
    }

    /**
     * 判断 IP 是否为空或无效占位（如 unknown）。
     *
     * @param ip IP 值
     * @return true 表示无效
     */
    private static boolean isInvalidIp(String ip) {
        return StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip.trim());
    }

    /**
     * 获取 User-Agent（截断至 255 字符）。
     *
     * @return User-Agent
     */
    public static String getUserAgent() {
        return StrUtil.maxLength(SaHolder.getRequest().getHeader("User-Agent"), USER_AGENT_MAX_LENGTH);
    }
}
