package org.njupt.njuptphysim.server.config;


import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.Interceptors.LoginInterceptor;
import org.njupt.njuptphysim.common.Interceptors.PermitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private PermitInterceptor permitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //todo 注册拦截器
        log.info("开始注册拦截器...");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")                             //设置拦截路径
                .excludePathPatterns("/common/login", "/error");    //设置排除路径

        registry.addInterceptor(permitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/common/**", "/error");
        log.info("完成注册拦截器...");
    }


}
