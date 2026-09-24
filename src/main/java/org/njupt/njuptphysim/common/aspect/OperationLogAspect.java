package org.njupt.njuptphysim.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.utils.IpUtil;
import org.njupt.njuptphysim.pojo.po.SysLogOperation;
import org.njupt.njuptphysim.pojo.po.SysLogRootEvent;
import org.njupt.njuptphysim.server.service.LogService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * {@link OperationLog} 用户操作日志切面。
 *
 * <p>拦截所有标注 @OperationLog 的 Controller 方法：
 * <ul>
 *   <li>控制台输出哪个用户什么时间干了什么（由 LogService.saveLog 输出）；</li>
 *   <li>自动调用 saveLog 将操作写入 sys_log_operation 表持久化；</li>
 *   <li>管理员接口（/admin/**）额外写入 sys_log_root_event 管理员事件表。</li>
 * </ul>
 * 日志写入为旁路能力，切面内部异常不会影响业务请求。</p>
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    /** 操作列（varchar(50)）最大长度 */
    private static final int MAX_OPERATION_LENGTH = 50;

    /** 请求参数（text）截断长度 */
    private static final int MAX_PARAMS_LENGTH = 2000;

    /** 事件内容（varchar(200)）最大长度 */
    private static final int MAX_EVENT_DETAIL_LENGTH = 200;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        HttpServletRequest request = currentRequest();

        LocalDateTime begin = LocalDateTime.now();
        long beginMillis = System.currentTimeMillis();
        Throwable error = null;
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            try {
                long costMillis = System.currentTimeMillis() - beginMillis;
                boolean success = (error == null);
                saveOperationLog(request, operationLog, joinPoint, begin, costMillis, success);
                if (request != null && request.getRequestURI().startsWith("/admin/")) {
                    saveAdminRootEvent(request, operationLog, begin, costMillis, success);
                }
            } catch (Exception logError) {
                log.warn("操作日志记录失败(不影响业务): {}", logError.getMessage());
            }
        }
    }

    /**
     * 写入 sys_log_operation 操作日志
     */
    private void saveOperationLog(HttpServletRequest request, OperationLog operationLog,
                                  ProceedingJoinPoint joinPoint,
                                  LocalDateTime begin, long costMillis, boolean success) {
        String userId = BaseContext.getCurrentId();
        String userName = resolveUserName(userId);

        SysLogOperation sysLog = SysLogOperation.builder()
                .operation(truncate(operationLog.value() + ":" + operationLog.detail(), MAX_OPERATION_LENGTH))
                .requestUri(request == null ? "-" : request.getRequestURI())
                .requestMethod(request == null ? "-" : request.getMethod())
                .requestParams(truncate(buildParams(joinPoint), MAX_PARAMS_LENGTH))
                .requestTime((int) costMillis)
                .ip(request == null ? "-" : IpUtil.getClientIp(request))
                .status(success ? 1 : 0)
                .creatorName(userName)
                .creator(userId)
                .createDate(begin)
                // 管理员操作记为系统事件(0)，其余记为业务事件(1)
                .type(request != null && request.getRequestURI().startsWith("/admin/") ? "0" : "1")
                .build();

        logService.saveLog(sysLog);
    }

    /**
     * 管理员操作额外写入 sys_log_root_event 管理员事件表
     */
    private void saveAdminRootEvent(HttpServletRequest request, OperationLog operationLog,
                                    LocalDateTime begin, long costMillis, boolean success) {
        String userId = BaseContext.getCurrentId();
        String userName = resolveUserName(userId);
        String detail = truncate(String.format("管理员[%s] 执行了 %s（%s %s，耗时%dms，%s）",
                userName, operationLog.detail(), request.getMethod(), request.getRequestURI(),
                costMillis, success ? "成功" : "失败"), MAX_EVENT_DETAIL_LENGTH);

        logService.saveRootEvent(SysLogRootEvent.builder()
                .beginDate(begin)
                .endDate(LocalDateTime.now())
                .eventDetail(detail)
                .createDate(LocalDateTime.now())
                .build());
    }

    /**
     * 获取当前请求，非 Web 上下文（如单元测试调用）时返回 null
     */
    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    /**
     * 将当前登录用户id解析为姓名，解析失败回退为id或"未登录用户"
     */
    private String resolveUserName(String userId) {
        if (userId == null || userId.isEmpty()) {
            return "未登录用户";
        }
        try {
            String name = userService.getNameById(userId);
            return (name == null || name.isEmpty()) ? userId : name;
        } catch (Exception e) {
            return userId;
        }
    }

    /**
     * 序列化方法参数（过滤 Servlet 对象、文件等不可序列化参数）
     */
    private String buildParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        Object[] filtered = Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return null;
                    }
                    if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                            || arg instanceof MultipartFile || arg instanceof byte[]) {
                        return arg.getClass().getSimpleName();
                    }
                    return arg;
                })
                .toArray();
        try {
            return OBJECT_MAPPER.writeValueAsString(filtered);
        } catch (Exception e) {
            return Arrays.stream(filtered)
                    .map(a -> a == null ? "null" : String.valueOf(a))
                    .collect(Collectors.joining(", "));
        }
    }


    private String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max);
    }
}
