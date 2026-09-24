package org.njupt.njuptphysim.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 账号封禁记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBan implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 被封禁账号(users.id) */
    private String userId;

    /** 封禁原因 */
    private String reason;

    /** 1封禁中 0已解封 */
    private Integer status;

    private LocalDateTime banTime;

    private LocalDateTime unbanTime;

    /** 操作人 */
    private String operator;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
