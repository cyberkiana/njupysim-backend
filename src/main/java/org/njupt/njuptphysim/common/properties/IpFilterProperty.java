package org.njupt.njuptphysim.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * IP过滤配置属性类
 * 所有配置从application.yml中读取
 */
@Data
@Component
@ConfigurationProperties(prefix = "ip-filter")
public class IpFilterProperty {

    /**
     * 是否启用IP过滤（从yml读取：ip-filter.enabled）
     */
    private boolean enabled;

    /**
     * 是否使用白名单模式（从yml读取：ip-filter.use-whitelist）
     */
    private boolean useWhitelist;

    /**
     * 黑名单配置（从yml读取：ip-filter.blacklist）
     */
    private Blacklist blacklist = new Blacklist();

    /**
     * 白名单配置（从yml读取：ip-filter.whitelist）
     */
    private Whitelist whitelist = new Whitelist();

    /**
     * 排除过滤的URL路径（从yml读取：ip-filter.exclude-urls）
     */
    private String excludeUrls;

    /**
     * 黑名单内部类
     */
    @Data
    public static class Blacklist {
        /**
         * 黑名单IP列表（从yml读取：ip-filter.blacklist.ips）
         */
        private List<String> ips = new ArrayList<>();
    }

    /**
     * 白名单内部类
     */
    @Data
    public static class Whitelist {
        /**
         * 白名单IP列表（从yml读取：ip-filter.whitelist.ips）
         */
        private List<String> ips = new ArrayList<>();
    }

    /**
     * 获取排除URL列表
     */
    public List<String> getExcludeUrlList() {
        List<String> urlList = new ArrayList<>();
        if (excludeUrls != null && !excludeUrls.trim().isEmpty()) {
            String[] urls = excludeUrls.split(",");
            for (String url : urls) {
                String trimmedUrl = url.trim();
                if (!trimmedUrl.isEmpty()) {
                    urlList.add(trimmedUrl);
                }
            }
        }
        return urlList;
    }

    /**
     * 获取当前过滤模式描述
     */
    public String getModeDescription() {
        return useWhitelist ? "白名单模式" : "黑名单模式";
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        return enabled ? "已启用" : "已禁用";
    }
}
