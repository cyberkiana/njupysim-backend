package org.njupt.njuptphysim.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，拦截 Controller 层抛出的异常并统一转换为 Result 响应。
 * 业务异常（BaseException / RuntimeException）返回 HTTP 200 + code=0；
 * 请求协议类错误（参数缺失、JSON 解析失败、404、405 等）返回对应的 HTTP 状态码，响应体仍为 Result。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常（项目自定义基类）
     */
    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 数据库唯一键冲突
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKey(DuplicateKeyException e) {
        log.warn("唯一键冲突: {}", e.getMessage());
        return Result.error("数据已存在，请勿重复提交");
    }

    /**
     * 其余数据库访问异常，不向前端暴露 SQL 细节
     */
    @ExceptionHandler(DataAccessException.class)
    public Result handleDataAccess(DataAccessException e) {
        log.error("数据库访问异常", e);
        return Result.error("数据处理失败，请稍后重试");
    }

    /**
     * 参数校验失败：@Valid 校验 @RequestBody（MethodArgumentNotValidException）与表单绑定（BindException）均为此类型子类
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return Result.error(msg);
    }

    /**
     * Spring 6.1 方法级参数校验（如 @RequestParam 上的约束注解）失败
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result handleMethodValidation(HandlerMethodValidationException e) {
        String msg = e.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return Result.error(msg);
    }

    /**
     * 请求体 JSON 解析失败
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result> handleNotReadable(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.warn("请求体解析失败 [{}] {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return build(HttpStatus.BAD_REQUEST, "请求体格式错误，请检查 JSON 格式");
    }

    /**
     * 缺少必填请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getParameterName());
        return build(HttpStatus.BAD_REQUEST, "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型错误: {} 需要 {}", e.getName(), e.getRequiredType());
        return build(HttpStatus.BAD_REQUEST, "参数类型错误: " + e.getName());
    }

    /**
     * 静态资源 / 未映射路径（Spring Boot 3.2+）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result> handleNoResource(HttpServletRequest request, NoResourceFoundException e) {
        log.warn("请求路径不存在: {} {}", request.getMethod(), request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, "请求路径不存在");
    }

    /**
     * HTTP 方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方式不支持: {}", e.getMessage());
        return build(HttpStatus.METHOD_NOT_ALLOWED, "请求方式不支持: " + e.getMethod());
    }

    /**
     * Content-Type 不支持
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.warn("Content-Type 不支持: {}", e.getContentType());
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "不支持的 Content-Type: " + e.getContentType());
    }

    /**
     * 兜底：运行时异常（业务层直接 throw 的 RuntimeException，message 面向用户）
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        log.error("运行时异常 [{}] {}", request.getMethod(), request.getRequestURI(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 兜底：其余所有异常，不向客户端暴露内部细节
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleException(HttpServletRequest request, Exception e) {
        log.error("系统异常 [{}] {}", request.getMethod(), request.getRequestURI(), e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "系统繁忙，请稍后重试");
    }

    private ResponseEntity<Result> build(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(Result.error(status.value(), msg));
    }
}
