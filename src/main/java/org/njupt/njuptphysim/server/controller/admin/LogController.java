package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员日志查询：操作日志 / 登录日志 / 异常日志 / 管理员事件
 */
@RestController
@RequestMapping("/admin/logs")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 分页查询操作日志（sys_log_operation）
     */
    @OperationLog(value = "查询", detail = "查看系统操作日志")
    @GetMapping("/operation")
    public Result searchOperationLogs(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(buildPage(logService.searchOperationLogs(page, pageSize),
                logService.countOperationLogs()));
    }

    /**
     * 分页查询登录日志（sys_log_login）
     */
    @OperationLog(value = "查询", detail = "查看系统登录日志")
    @GetMapping("/login")
    public Result searchLoginLogs(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(buildPage(logService.searchLoginLogs(page, pageSize),
                logService.countLoginLogs()));
    }

    /**
     * 分页查询异常日志（sys_log_error）
     */
    @OperationLog(value = "查询", detail = "查看系统异常日志")
    @GetMapping("/error")
    public Result searchErrorLogs(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(buildPage(logService.searchErrorLogs(page, pageSize),
                logService.countErrorLogs()));
    }

    /**
     * 分页查询管理员事件（sys_log_root_event）
     */
    @OperationLog(value = "查询", detail = "查看管理员事件日志")
    @GetMapping("/rootEvent")
    public Result searchRootEvents(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(buildPage(logService.searchRootEvents(page, pageSize),
                logService.countRootEvents()));
    }

    private Map<String, Object> buildPage(Object list, int total) {
        Map<String, Object> page = new HashMap<>();
        page.put("list", list);
        page.put("total", total);
        return page;
    }
}
