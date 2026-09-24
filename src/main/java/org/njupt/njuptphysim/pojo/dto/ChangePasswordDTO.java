package org.njupt.njuptphysim.pojo.dto;

import lombok.Data;

/**
 * 用户在个人中心修改自己密码的请求体。
 * 不含用户id字段——目标用户一律由后端从登录令牌解析，防止越权修改他人密码。
 */
@Data
public class ChangePasswordDTO {

    /** 原密码 */
    private String oldPassword;

    /** 新密码 */
    private String newPassword;
}
