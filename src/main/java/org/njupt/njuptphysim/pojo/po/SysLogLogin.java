package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统登录日志（sys_log_login），登录/退出接口在方法内直接写入
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysLogLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** 用户操作(0:用户登录; 1:用户退出) */
    private Integer operation;

    /** 状态(0:失败; 1:成功; 2:账号已锁定) */
    private Integer status;

    /** 用户名 */
    private String creatorName;

    /** 创建时间 */
    private LocalDateTime createDate;
}
