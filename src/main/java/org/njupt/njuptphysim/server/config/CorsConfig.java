package org.njupt.njuptphysim.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，配置跨域设置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("POST","PUT","DELETE","GET")
                .allowedHeaders("*")        // 需要配置允许请求头，不然使用jwt时预检请求会被拒收
                .allowCredentials(true)
                .maxAge(3600);
    }
}
