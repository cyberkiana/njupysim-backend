package org.njupt.njuptphysim.common.handler;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.pojo.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(RuntimeException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception e, HttpServletRequest request) {
        System.err.println("========== 异常详情 ==========");
        System.err.println("请求方法: " + request.getMethod());
        System.err.println("请求路径: " + request.getRequestURI());
        System.err.println("异常类型: " + e.getClass().getName());
        System.err.println("异常信息: " + e.getMessage());
        e.printStackTrace();

        // 获取可能的状态码
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        System.err.println("状态码: " + statusCode);

        Map<String, Object> error = new HashMap<>();
        error.put("code", statusCode != null ? statusCode : 500);
        error.put("msg", e.getMessage());
        error.put("method", request.getMethod());

        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        System.err.println("========== 请求体解析失败 ==========");
        System.err.println("请求路径: " + request.getRequestURI());
        System.err.println("请求方法: " + request.getMethod());
        System.err.println("Content-Type: " + request.getHeader("Content-Type"));

        // 尝试读取请求体
        try {
            String body = request.getReader().lines().collect(Collectors.joining());
            System.err.println("实际请求体: " + body);
        } catch (IOException | java.io.IOException ioException) {
            System.err.println("无法读取请求体");
        }

        System.err.println("异常信息: " + e.getMessage());
        e.printStackTrace();

        Map<String, Object> error = new HashMap<>();
        error.put("code", 400);
        error.put("msg", "请求体格式错误，请检查 JSON 格式");

        return ResponseEntity.badRequest().body(error);
    }

}
