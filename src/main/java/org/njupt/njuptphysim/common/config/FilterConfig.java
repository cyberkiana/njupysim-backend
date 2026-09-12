package org.njupt.njuptphysim.common.config;

import org.njupt.njuptphysim.common.filters.IpFilter;
import org.njupt.njuptphysim.common.properties.IpFilterProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet 过滤器注册配置。
 */
@Configuration
public class FilterConfig {

    /**
     * 注册 IP 过滤器，并由配置属性控制其启用状态。
     */
    @Bean
    public FilterRegistrationBean<IpFilter> ipFilterRegistration(IpFilterProperty ipFilterProperty) {
        FilterRegistrationBean<IpFilter> registration = new FilterRegistrationBean<>();

        // 由 FilterConfig 统一创建过滤器，避免与 @Component 自动注册重复
        registration.setFilter(new IpFilter(ipFilterProperty));

        // 拦截所有请求，具体放行/拦截逻辑由 IpFilter 内部处理
        registration.addUrlPatterns("/*");

        // 设置过滤器名称和执行顺序，数字越小越先执行
        registration.setName("ipFilter");
        registration.setOrder(1);

        // 是否启用该过滤器，跟随 application.yml ip-filter.enabled
        registration.setEnabled(ipFilterProperty.isEnabled());

        // 启用异步支持
        registration.setAsyncSupported(true);

        return registration;
    }
}
