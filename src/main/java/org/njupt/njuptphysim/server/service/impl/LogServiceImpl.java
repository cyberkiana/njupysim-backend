package org.njupt.njuptphysim.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.pojo.po.SysLogError;
import org.njupt.njuptphysim.pojo.po.SysLogLogin;
import org.njupt.njuptphysim.pojo.po.SysLogOperation;
import org.njupt.njuptphysim.pojo.po.SysLogRootEvent;
import org.njupt.njuptphysim.server.mapper.LogMapper;
import org.njupt.njuptphysim.server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统日志服务实现：每张日志表写入时同步输出控制台日志。
 * 写库失败仅告警不影响主流程（日志是旁路能力）。
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 异常堆栈入库时的最大长度，防止 text 列过大 */
    private static final int MAX_ERROR_LENGTH = 4000;

    @Autowired
    private LogMapper logMapper;

    /**
     * 保存操作日志（@OperationLog 切面自动调用），同时控制台输出。
     * REQUIRES_NEW：日志独立提交，不随业务事务回滚。
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(SysLogOperation sysLog) {
        try {
            logMapper.insertOperationLog(sysLog);
        } catch (Exception e) {
            log.warn("操作日志入库失败: {}", e.getMessage());
        }
        // 控制台输出：哪个用户什么时间干了什么
        log.info("[操作日志] 用户[{}] 于{} 执行了【{}】(类型：{}，URI：{} {}，耗时：{}ms，结果：{})",
                sysLog.getCreatorName(),
                sysLog.getCreateDate() == null ? LocalDateTime.now().format(FORMATTER) : sysLog.getCreateDate().format(FORMATTER),
                sysLog.getOperation(),
                sysLog.getType() != null && sysLog.getType().equals("0") ? "系统事件" : "业务事件",
                sysLog.getRequestMethod(),
                sysLog.getRequestUri(),
                sysLog.getRequestTime(),
                sysLog.getStatus() != null && sysLog.getStatus() == 1 ? "成功" : "失败");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLoginLog(int operation, int status, String creatorName) {
        String opName = operation == 0 ? "登录" : "退出";
        String statusName = status == 1 ? "成功" : (status == 2 ? "账号已锁定" : "失败");
        try {
            logMapper.insertLoginLog(SysLogLogin.builder()
                    .operation(operation)
                    .status(status)
                    .creatorName(creatorName)
                    .createDate(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.warn("登录日志入库失败: {}", e.getMessage());
        }
        log.info("[登录日志] 用户[{}] 于{} {}{}",
                creatorName, LocalDateTime.now().format(FORMATTER), opName, statusName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(Exception e) {
        String creator = BaseContext.getCurrentId();
        try {
            logMapper.insertErrorLog(SysLogError.builder()
                    .errorInfo(truncate(buildErrorInfo(e), MAX_ERROR_LENGTH))
                    .creator(creator)
                    .createDate(LocalDateTime.now())
                    .build());
        } catch (Exception ex) {
            log.warn("异常日志入库失败: {}", ex.getMessage());
        }
        log.error("[异常日志] 于{} 发生异常：{}",
                LocalDateTime.now().format(FORMATTER), e.getMessage());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRootEvent(SysLogRootEvent event) {
        if (event.getCreateDate() == null) {
            event.setCreateDate(LocalDateTime.now());
        }
        try {
            logMapper.insertRootEvent(event);
        } catch (Exception e) {
            log.warn("管理员事件入库失败: {}", e.getMessage());
        }
        log.info("[管理员事件] {}（{} ~ {}）",
                event.getEventDetail(),
                event.getBeginDate() == null ? "-" : event.getBeginDate().format(FORMATTER),
                event.getEndDate() == null ? "-" : event.getEndDate().format(FORMATTER));
    }

    @Override
    public List<SysLogOperation> searchOperationLogs(int page, int pageSize) {
        return logMapper.searchOperationLogs((page - 1) * pageSize, pageSize);
    }

    @Override
    public int countOperationLogs() {
        return logMapper.countOperationLogs();
    }

    @Override
    public List<SysLogLogin> searchLoginLogs(int page, int pageSize) {
        return logMapper.searchLoginLogs((page - 1) * pageSize, pageSize);
    }

    @Override
    public int countLoginLogs() {
        return logMapper.countLoginLogs();
    }

    @Override
    public List<SysLogError> searchErrorLogs(int page, int pageSize) {
        return logMapper.searchErrorLogs((page - 1) * pageSize, pageSize);
    }

    @Override
    public int countErrorLogs() {
        return logMapper.countErrorLogs();
    }

    @Override
    public List<SysLogRootEvent> searchRootEvents(int page, int pageSize) {
        return logMapper.searchRootEvents((page - 1) * pageSize, pageSize);
    }

    @Override
    public int countRootEvents() {
        return logMapper.countRootEvents();
    }


    /**
     * 拼接异常摘要：异常类名 + message + 堆栈前几行
     */
    private String buildErrorInfo(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String userId = BaseContext.getCurrentId();
        String userPart = userId == null ? "未登录用户" : "用户[" + userId + "]";
        return userPart + " 触发异常 " + e.getClass().getName() + ": " + e.getMessage() + "\n" + sw;
    }

    private String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...(truncated)";
    }
}
