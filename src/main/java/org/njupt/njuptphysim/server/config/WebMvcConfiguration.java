package org.njupt.njuptphysim.server.config;


import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.Interceptors.LoginInterceptor;
import org.njupt.njuptphysim.common.Interceptors.PermitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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

    /** WebGL实验文件存放目录（服务器独立文件夹），与 webgl.upload-dir 配置一致 */
    @Value("${webgl.upload-dir:./webgl-files}")
    private String webglUploadDir;

    /** 实验头图存放目录（服务器独立文件夹），与 resource.img-dir 配置一致 */
    @Value("${resource.img-dir:./img}")
    private String imgUploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //todo 注册拦截器
        log.info("开始注册拦截器...");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")                             //设置拦截路径
                .excludePathPatterns("/common/login", "/error", "/webgl/**", "/img/**");    //设置排除路径

        registry.addInterceptor(permitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/common/**", "/error", "/webgl/**", "/img/**");
        log.info("完成注册拦截器...");
    }

    /**
     * WebGL实验文件静态访问：GET /webgl/** 直接映射到服务器独立文件夹，
     * 与上传接口(/admin/res/upload-webgl)配合使用；该路径不经过登录拦截器，
     * 供 WebGL 运行时直接加载资源。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webgl/**")
                .addResourceLocations("file:" + java.nio.file.Paths.get(webglUploadDir).toAbsolutePath().normalize() + "/");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + java.nio.file.Paths.get(imgUploadDir).toAbsolutePath().normalize() + "/");
    }


}
