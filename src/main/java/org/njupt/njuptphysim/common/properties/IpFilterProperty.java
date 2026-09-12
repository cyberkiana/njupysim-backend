package org.njupt.njuptphysim.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * IP 过滤配置属性类。
 *
 * <p>对应 {@code application.yml} 中 {@code ip-filter} 前缀下的配置。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "ip-filter")
public class IpFilterProperty {

    /**
     * 是否启用 IP 过滤（对应 ip-filter.enabled）。
     */
    private boolean enabled = true;

    /**
     * 过滤模式：true 表示白名单模式，false 表示黑名单模式。
     */
    private boolean useWhitelist;

    /**
     * 黑名单 IP 规则（对应 ip-filter.blacklist.ips）。
     */
    private Blacklist blacklist = new Blacklist();

    /**
     * 白名单 IP 规则（对应 ip-filter.whitelist.ips）。
     */
    private Whitelist whitelist = new Whitelist();

    /**
     * 需要跳过 IP 过滤的 URL 路径（对应 ip-filter.exclude-urls）。
     */
    private List<String> excludeUrls = new ArrayList<>();

    /**
     * 黑名单配置。
     */
    @Data
    public static class Blacklist {
        /**
         * 黑名单 IP 规则列表。
         */
        private List<String> ips = new ArrayList<>();
    }

    /**
     * 白名单配置。
     */
    @Data
    public static class Whitelist {
        /**
         * 白名单 IP 规则列表。
         */
        private List<String> ips = new ArrayList<>();
    }

    /**
     * 获取清洗后的黑名单 IP 规则。
     */
    public List<String> getBlacklistIps() {
        return cleanRules(blacklist == null ? List.of() : blacklist.getIps());
    }

    /**
     * 获取清洗后的白名单 IP 规则。
     */
    public List<String> getWhitelistIps() {
        return cleanRules(whitelist == null ? List.of() : whitelist.getIps());
    }

    /**
     * 获取清洗后的排除 URL 列表。
     */
    public List<String> getExcludeUrlList() {
        return cleanRules(excludeUrls);
    }

    /**
     * 去空、去首尾空格并去重。
     */
    private List<String> cleanRules(List<String> rules) {
        if (rules == null) {
            return List.of();
        }

        return rules.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(rule -> !rule.isEmpty())
                .distinct()
                .toList();
    }

    /**
     * 获取当前过滤模式描述。
     */
    public String getModeDescription() {
        return useWhitelist ? "白名单模式" : "黑名单模式";
    }

    /**
     * 获取启用状态描述。
     */
    public String getStatusDescription() {
        return enabled ? "已启用" : "已禁用";
    }
}
