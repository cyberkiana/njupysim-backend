package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.dto.LoginDTO;
import org.njupt.njuptphysim.pojo.vo.LoginVO;

public interface LoginService {

    /**
     * 登录：登录结果（成功/失败）直接持久化到 sys_log_login
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 退出登录：记录退出日志（sys_log_login operation=1）
     */
    void logout();
}
