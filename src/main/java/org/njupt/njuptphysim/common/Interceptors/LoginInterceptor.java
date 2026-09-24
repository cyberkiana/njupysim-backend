package org.njupt.njuptphysim.common.Interceptors;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.properties.JwtProperty;
import org.njupt.njuptphysim.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


//登录拦截器
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperty jwtProperty;

    @Autowired
    private org.njupt.njuptphysim.server.service.UserBanService userBanService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        // 预检option直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        //1. 获取请求uri。
        String uri = request.getRequestURI();
        log.info("请求路径: {}", uri);

        //2. 判断是否是登录路径（冗余设计，实际在拦截器注册时已排除）
        if (uri.equals("/common/login")) {
            log.info("登录请求，直接放行");
            return true;
        }


        //3. 获取请求头中的token令牌。
        String authHeader = request.getHeader("Authorization");
        String token = "";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 去掉 "Bearer " 前缀
        }
        log.info("token:{}", token);


        //4. 判断令牌是否存在，如果不存在，返回错误结果（未登录）。
        if (!StringUtils.hasLength(token)) { //jwt为空
            log.info("获取到jwt令牌为空, 返回错误结果");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            return false;
        }

        //5. 解析token，如果解析失败，返回错误结果（未登录）。
        Claims parseJWT;
        try {
            parseJWT = JwtUtil.parseJWT(jwtProperty.getSecretKey(), token);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析令牌失败, 返回错误结果");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            return false;
        }

        //6.判断令牌是否超时
        if (JwtUtil.isTokenExpired(parseJWT)){
            log.info("令牌超时");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            return false;
        }

        //7. 放行。
        log.info("令牌合法, 放行");

        //8.设置当前线程的用户id
        BaseContext.setCurrentId(parseJWT.get("id").toString());

        //8.5 校验账号是否被封禁。必须在 setCurrentId 之后执行,
        //否则上下文无用户id, 封禁检查恒为假(曾因此失效, 勿调整顺序)
        if (userBanService.getActiveUserIds().contains(BaseContext.getCurrentId())) {
            log.info("账号[{}]已被封禁, 拒绝访问", BaseContext.getCurrentId());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"该账号已被封禁，请联系管理员\"}");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        // 删除线程上下文内的id，防止内存泄露
        BaseContext.removeCurrentId();
    }
}
