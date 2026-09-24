package org.njupt.njuptphysim.common.Interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.server.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


//权限拦截器
@Slf4j
@Component
public class PermitInterceptor implements HandlerInterceptor {
    @Autowired
    private RolesService rolesService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        response.setContentType("application/json;charset=UTF-8");

        //1. 获取用户id和角色
        String UserId = BaseContext.getCurrentId();
        String roleName = rolesService.getRoleNameByUserId(UserId);
        //2. 获取请求uri（getRequestURI 返回路径部分，用于前缀匹配）
        String url = request.getRequestURI();
        //3. 判断角色权限是否满足url访问权限
        if(url.startsWith("/admin/")&& roleName.equals("admin")){
            return true;
        }
        if(url.startsWith("/tea/")&& roleName.equals("teacher")){
            return true;
        }
        if(url.startsWith("/stu/")&& roleName.equals("student")){
            return true;
        }
        log.info("权限不足");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
