package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志（sys_log_operation），由 @OperationLog 切面写入
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysLogOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 用户操作，取注解的 value（操作类型） + detail（操作名称） */
    private String operation;

    /** 请求URI */
    private String requestUri;

    /** 请求方式 GET/POST/... */
    private String requestMethod;

    /** 请求参数 */
    private String requestParams;

    /** 请求时长(毫秒) */
    private Integer requestTime;

    /** 操作IP */
    private String ip;

    /** 状态(0:失败; 1:成功) */
    private Integer status;

    /** 用户名 */
    private String creatorName;

    /** 创建者id（users.id 为字符串学号，非数字时存 null） */
    private String creator;

    /** 创建时间 */
    private LocalDateTime createDate;

    /** 日志类型(0:系统事件 1:业务事件) */
    private String type;
}
