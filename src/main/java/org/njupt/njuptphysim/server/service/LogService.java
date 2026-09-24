package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.po.SysLogError;
import org.njupt.njuptphysim.pojo.po.SysLogLogin;
import org.njupt.njuptphysim.pojo.po.SysLogOperation;
import org.njupt.njuptphysim.pojo.po.SysLogRootEvent;

import java.util.List;

/**
 * 系统日志服务：四张日志表的写入（含控制台输出）与管理员查询
 */
public interface LogService {

    /**
     * 保存操作日志（@OperationLog 切面自动调用），同时控制台输出
     */
    void saveLog(SysLogOperation log);

    /**
     * 保存登录/退出日志，同时控制台输出
     *
     * @param operation 0:用户登录 1:用户退出
     * @param status    0:失败 1:成功 2:账号已锁定
     * @param creatorName 用户名（账号）
     */
    void saveLoginLog(int operation, int status, String creatorName);

    /**
     * 保存异常日志，同时控制台输出
     *
     * @param e 触发的异常
     */
    void saveErrorLog(Exception e);

    /**
     * 保存管理员事件日志（管理员操作/定时任务），同时控制台输出
     */
    void saveRootEvent(SysLogRootEvent event);

    /**
     * 分页查询操作日志
     */
    List<SysLogOperation> searchOperationLogs(int page, int pageSize);

    /**
     * 操作日志总数
     */
    int countOperationLogs();

    /**
     * 分页查询登录日志
     */
    List<SysLogLogin> searchLoginLogs(int page, int pageSize);

    /**
     * 登录日志总数
     */
    int countLoginLogs();

    /**
     * 分页查询异常日志
     */
    List<SysLogError> searchErrorLogs(int page, int pageSize);

    /**
     * 异常日志总数
     */
    int countErrorLogs();

    /**
     * 分页查询管理员事件
     */
    List<SysLogRootEvent> searchRootEvents(int page, int pageSize);

    /**
     * 管理员事件总数
     */
    int countRootEvents();
}
