package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员事件日志（sys_log_root_event），管理员操作与定时任务写入
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysLogRootEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 开始时间 */
    private LocalDateTime beginDate;

    /** 结束时间 */
    private LocalDateTime endDate;

    /** 事件内容 */
    private String eventDetail;

    /** 创建时间 */
    private LocalDateTime createDate;
}
