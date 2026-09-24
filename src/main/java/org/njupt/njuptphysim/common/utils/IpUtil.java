package org.njupt.njuptphysim.common.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 客户端IP获取工具。
 *
 * <p>适配 nginx 反向代理部署：优先取 nginx 设置的 X-Real-IP（nginx 总是用真实客户端地址
 * 覆盖该头，不可伪造）；其次取 X-Forwarded-For 的<b>最后一个</b>值（由最近一跳可信代理追加），
 * 避免取第一个值被客户端伪造绕过 IP 黑名单/登录限流；都没有时回退 getRemoteAddr。</p>
 */
public class IpUtil {

    private static final String UNKNOWN_IP = "unknown";

    private IpUtil() {
    }

    /**
     * 获取客户端真实IP
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN_IP;
        }
        String ip = firstValid(
                cleanHeader(request.getHeader("X-Real-IP")),
                lastOfXff(request.getHeader("X-Forwarded-For")),
                cleanHeader(request.getHeader("Proxy-Client-IP")),
                cleanHeader(request.getHeader("WL-Proxy-Client-IP")));
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.trim() : UNKNOWN_IP;
    }

    /**
     * X-Forwarded-For 形如 "客户端, 代理1, 代理2"，取最后一项（最近一跳可信代理追加的真实地址）
     */
    private static String lastOfXff(String xff) {
        String value = cleanHeader(xff);
        if (value == null) {
            return null;
        }
        String[] parts = value.split(",");
        return cleanHeader(parts[parts.length - 1]);
    }

    private static String cleanHeader(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return (trimmed.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(trimmed)) ? null : trimmed;
    }

    private static String firstValid(String... candidates) {
        for (String c : candidates) {
            if (c != null) {
                return c;
            }
        }
        return null;
    }
}
