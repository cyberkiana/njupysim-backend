package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.njupt.njuptphysim.pojo.po.SysLogError;
import org.njupt.njuptphysim.pojo.po.SysLogLogin;
import org.njupt.njuptphysim.pojo.po.SysLogOperation;
import org.njupt.njuptphysim.pojo.po.SysLogRootEvent;

import java.util.List;

/**
 * 系统日志 Mapper：四张日志表（登录/操作/异常/管理员事件）的写入与查询
 */
@Mapper
public interface LogMapper {

    // ==================== sys_log_operation 系统操作日志 ====================

    /**
     * 写入操作日志
     */
    @Insert("insert into njupt_physim.sys_log_operation " +
            "(operation, request_uri, request_method, request_params, request_time, ip, status, creator_name, creator, type, create_date) " +
            "values (#{log.operation}, #{log.requestUri}, #{log.requestMethod}, #{log.requestParams}, #{log.requestTime}, " +
            "#{log.ip}, #{log.status}, #{log.creatorName}, #{log.creator}, #{log.type}, #{log.createDate})")
    void insertOperationLog(@Param("log") SysLogOperation log);

    /**
     * 分页查询操作日志（按创建时间倒序）
     */
    @Select("select * from njupt_physim.sys_log_operation order by create_date desc limit #{offset}, #{pageSize}")
    List<SysLogOperation> searchOperationLogs(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 操作日志总数
     */
    @Select("select count(*) from njupt_physim.sys_log_operation")
    int countOperationLogs();

    // ==================== sys_log_login 系统登录日志 ====================

    /**
     * 写入登录/退出日志
     */
    @Insert("insert into njupt_physim.sys_log_login (operation, status, creator_name, create_date) " +
            "values (#{log.operation}, #{log.status}, #{log.creatorName}, #{log.createDate})")
    void insertLoginLog(@Param("log") SysLogLogin log);

    /**
     * 分页查询登录日志（按创建时间倒序）
     */
    @Select("select * from njupt_physim.sys_log_login order by create_date desc limit #{offset}, #{pageSize}")
    List<SysLogLogin> searchLoginLogs(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 登录日志总数
     */
    @Select("select count(*) from njupt_physim.sys_log_login")
    int countLoginLogs();

    // ==================== sys_log_error 系统异常日志 ====================

    /**
     * 写入异常日志
     */
    @Insert("insert into njupt_physim.sys_log_error (error_info, creator, create_date) " +
            "values (#{log.errorInfo}, #{log.creator}, #{log.createDate})")
    void insertErrorLog(@Param("log") SysLogError log);

    /**
     * 分页查询异常日志（按创建时间倒序）
     */
    @Select("select * from njupt_physim.sys_log_error order by create_date desc limit #{offset}, #{pageSize}")
    List<SysLogError> searchErrorLogs(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 异常日志总数
     */
    @Select("select count(*) from njupt_physim.sys_log_error")
    int countErrorLogs();

    // ==================== sys_log_root_event 管理员事件管理 ====================

    /**
     * 写入管理员事件（管理员操作/定时任务）
     */
    @Insert("insert into njupt_physim.sys_log_root_event (begin_date, end_date, event_detail, create_date) " +
            "values (#{log.beginDate}, #{log.endDate}, #{log.eventDetail}, #{log.createDate})")
    void insertRootEvent(@Param("log") SysLogRootEvent log);

    /**
     * 分页查询管理员事件（按创建时间倒序）
     */
    @Select("select * from njupt_physim.sys_log_root_event order by create_date desc limit #{offset}, #{pageSize}")
    List<SysLogRootEvent> searchRootEvents(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 管理员事件总数
     */
    @Select("select count(*) from njupt_physim.sys_log_root_event")
    int countRootEvents();
}
