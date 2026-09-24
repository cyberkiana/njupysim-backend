package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统异常日志（sys_log_error），由全局异常处理器写入
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysLogError implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 异常信息（含堆栈摘要） */
    private String errorInfo;

    /** 创建者（触发请求的用户id，非数字或未登录时为 null） */
    private String creator;

    /** 创建时间 */
    private LocalDateTime createDate;
}
