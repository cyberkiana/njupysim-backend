package org.njupt.njuptphysim.common.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.properties.IpFilterProperty;
import org.njupt.njuptphysim.common.utils.IpUtil;
import org.njupt.njuptphysim.server.service.IpBlacklistService;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

/**
 * IP 访问过滤 Servlet 过滤器。
 *
 * <p>支持两种过滤模式：白名单模式只放行命中的 IP，黑名单模式只拦截命中的 IP。
 * 黑名单从数据库表 sys_ip_blacklist 动态读取（管理员可在页面查看并解封），
 * 白名单仍来自 application.yml 配置。
 * 名单规则支持精确 IP、IPv4 通配符和 CIDR 网段，URL 排除规则使用 Ant 路径匹配。</p>
 */
@Slf4j
public class IpFilter implements Filter {

    private static final String UNKNOWN_IP = "unknown";

    private final IpFilterProperty ipFilterProperty;

    private final IpBlacklistService ipBlacklistService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public IpFilter(IpFilterProperty ipFilterProperty, IpBlacklistService ipBlacklistService) {
        this.ipFilterProperty = ipFilterProperty;
        this.ipBlacklistService = ipBlacklistService;
    }

    /**
     * 初始化过滤器，输出当前配置便于排查。
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("========== IpFilter初始化完成 ==========");
        log.info("IP过滤状态：{}", ipFilterProperty.getStatusDescription());
        log.info("IP过滤模式：{}", ipFilterProperty.getModeDescription());
        log.info("数据库黑名单IP列表：{}", ipBlacklistService.getActiveIps());
        log.info("白名单IP列表：{}", ipFilterProperty.getWhitelistIps());
        log.info("排除URL列表：{}", ipFilterProperty.getExcludeUrlList());
        log.info("==========================================");
    }

    /**
     * 过滤器销毁。
     */
    @Override
    public void destroy() {
        log.info("IpFilter销毁");
    }

    /**
     * 过滤器主流程：先处理排除 URL，再根据启用状态执行 IP 名单校验。
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientIp = IpUtil.getClientIp(request);
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        log.debug("请求信息 - IP: {}, Method: {}, URI: {}", clientIp, method, requestUri);

        // 检查是否在排除URL列表中
        if (isExcludedUrl(requestUri)) {
            log.debug("URL: {} 在排除列表中，跳过IP过滤", requestUri);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // IP过滤逻辑
        if (ipFilterProperty.isEnabled()) {
            if (!isIpAllowed(clientIp)) {
                log.warn("IP已被拦截 - IP: {}, Method: {}, URI: {}", clientIp, method, requestUri);
                sendForbiddenResponse(response, clientIp);
                return;
            }
            log.debug("IP验证通过 - IP: {}", clientIp);
        }

        // 验证通过，继续执行后续过滤器或请求
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 发送 403 JSON 禁止访问响应。
     */
    private void sendForbiddenResponse(HttpServletResponse response, String clientIp) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        String mode = ipFilterProperty.isUseWhitelist() ? "不在白名单中" : "在黑名单中";
        String errorMsg = String.format(
                "{\"code\":403,\"message\":\"您的IP(%s)%s，已被限制访问\",\"timestamp\":%d}",
                clientIp, mode, System.currentTimeMillis()
        );

        response.getWriter().write(errorMsg);
    }

    /**
     * 判断请求 URI 是否命中排除列表，命中时跳过 IP 过滤。
     */
    private boolean isExcludedUrl(String requestUri) {
        List<String> excludeUrls = ipFilterProperty.getExcludeUrlList();

        if (excludeUrls.isEmpty()) {
            return false;
        }

        for (String excludeUrl : excludeUrls) {
            // 支持Ant风格的路径匹配
            if (pathMatcher.match(excludeUrl, requestUri)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断 IP 是否允许访问。
     *
     * <p>白名单模式下，只有命中名单的 IP 才能访问；黑名单模式下，命中名单的 IP 会被拦截。
     * 无法识别客户端 IP 时按安全策略默认拒绝。</p>
     */
    private boolean isIpAllowed(String clientIp) {
        if (clientIp == null || UNKNOWN_IP.equalsIgnoreCase(clientIp)) {
            log.warn("无法获取客户端IP，默认拒绝访问");
            return false;
        }

        if (ipFilterProperty.isUseWhitelist()) {
            // 白名单模式：只有在白名单中的 IP 才能访问
            boolean allowed = isIpInList(clientIp, ipFilterProperty.getWhitelistIps());
            if (!allowed) {
                log.warn("IP: {} 不在白名单中", clientIp);
            }
            return allowed;
        } else {
            // 黑名单模式：在黑名单中的 IP 不能访问（黑名单来自数据库表 sys_ip_blacklist）
            boolean blocked = isIpInList(clientIp, ipBlacklistService.getActiveIps());
            if (blocked) {
                log.warn("IP: {} 在数据库黑名单中", clientIp);
            }
            return !blocked;
        }
    }

    /**
     * 判断 IP 是否命中名单。
     *
     * <p>名单条目支持：精确 IP（192.168.1.100）、通配符（192.168.1.*、192.168.*.*）、
     * CIDR 网段（192.168.1.0/24）。</p>
     */
    private boolean isIpInList(String clientIp, List<String> ipList) {
        if (clientIp == null || ipList == null || ipList.isEmpty()) {
            return false;
        }

        String normalizedIp = clientIp.trim();
        for (String ipPattern : ipList) {
            if (ipPattern == null) {
                continue;
            }

            String trimmedPattern = ipPattern.trim();
            if (trimmedPattern.isEmpty()) {
                continue;
            }

            if (isIpPatternMatch(normalizedIp, trimmedPattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 按单条规则匹配 IP。
     */
    private boolean isIpPatternMatch(String clientIp, String ipPattern) {
        // 精确 IP 匹配
        if (ipPattern.equals(clientIp)) {
            return true;
        }

        // 通配符匹配：* 可以替代任意一个 IPv4 段
        if (ipPattern.contains("*")) {
            return matchWildcardIp(clientIp, ipPattern);
        }

        // CIDR 网段匹配
        if (ipPattern.contains("/")) {
            return isIpInCidrRange(clientIp, ipPattern);
        }

        return false;
    }

    /**
     * 通配符规则匹配，例如 192.168.1.*、192.168.*.*。
     */
    private boolean matchWildcardIp(String clientIp, String wildcardPattern) {
        if (!isValidIp(clientIp) || !isValidWildcardPattern(wildcardPattern)) {
            return false;
        }

        String[] clientParts = clientIp.split("\\.");
        String[] patternParts = wildcardPattern.split("\\.");

        for (int i = 0; i < clientParts.length; i++) {
            if (!"*".equals(patternParts[i]) && !patternParts[i].equals(clientParts[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * 校验 IPv4 通配符规则：必须是四段，且非 * 段必须是 0-255。
     */
    private boolean isValidWildcardPattern(String wildcardPattern) {
        if (wildcardPattern == null) {
            return false;
        }

        String[] patternParts = wildcardPattern.split("\\.");
        if (patternParts.length != 4) {
            return false;
        }

        for (String patternPart : patternParts) {
            if ("*".equals(patternPart)) {
                continue;
            }

            try {
                int value = Integer.parseInt(patternPart);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断 IP 是否在指定的 IPv4 CIDR 网段内。
     */
    private boolean isIpInCidrRange(String ip, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            String networkIp = parts[0].trim();
            int prefixLength = Integer.parseInt(parts[1].trim());

            if (prefixLength < 0 || prefixLength > 32) {
                log.warn("无效的CIDR前缀长度: {}", prefixLength);
                return false;
            }

            // 验证客户端 IP 和网段地址格式
            if (!isValidIp(ip) || !isValidIp(networkIp)) {
                log.warn("无效的IP格式 - ClientIP: {}, NetworkIP: {}", ip, networkIp);
                return false;
            }

            long ipLong = ipToLong(ip);
            long networkLong = ipToLong(networkIp);
            long mask = prefixLength == 0 ? 0L : (0xFFFFFFFFL << (32 - prefixLength));

            return (ipLong & mask) == (networkLong & mask);
        } catch (Exception e) {
            log.error("CIDR范围检查出错 - IP: {}, CIDR: {}", ip, cidr, e);
            return false;
        }
    }

    /**
     * 验证 IPv4 地址格式，要求为四个 0-255 的十进制段。
     */
    private boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 将 IPv4 地址转换为 long 类型，便于进行 CIDR 位运算。
     */
    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        long result = 0;
        for (String octet : octets) {
            result = (result << 8) | Long.parseLong(octet);
        }
        return result;
    }
}
