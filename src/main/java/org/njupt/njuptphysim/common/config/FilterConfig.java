package org.njupt.njuptphysim.common.config;

import org.njupt.njuptphysim.common.filters.IpFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private IpFilter ipFilter;

    @Bean
    public FilterRegistrationBean<IpFilter> tokenFilterRegistration() {
        FilterRegistrationBean<IpFilter> registration = new FilterRegistrationBean<>();

        // 注入TokenFilter实例
        registration.setFilter(ipFilter);

        // 设置拦截的URL模式
        registration.addUrlPatterns("/*");

        // 设置过滤器名称
        registration.setName("ipFilter");

        // 设置过滤器执行顺序（数字越小越先执行）
        registration.setOrder(1);

        // 是否启用异步支持
        registration.setAsyncSupported(true);

        return registration;
    }
}
