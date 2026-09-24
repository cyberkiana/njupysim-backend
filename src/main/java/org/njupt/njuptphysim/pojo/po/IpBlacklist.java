package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * IP黑名单（sys_ip_blacklist），替代 yml 固定黑名单配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpBlacklist implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** IP地址规则（支持精确IP、通配符、CIDR） */
    private String ip;

    /** 封禁原因 */
    private String reason;

    /** 状态(1:封禁中 0:已解封) */
    private Integer status;

    /** 封禁时间 */
    private LocalDateTime banTime;

    /** 解封时间 */
    private LocalDateTime unbanTime;

    /** 操作人(系统自动/管理员) */
    private String operator;

    /** 创建时间 */
    private LocalDateTime createDate;

    /** 更新时间 */
    private LocalDateTime updateDate;
}
