package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.dto.LoginDTO;
import org.njupt.njuptphysim.pojo.vo.LoginVO;

public interface LoginService {

    /**
     * 登录
     * @param loginDTO
     * @return
     */
    LoginVO login(LoginDTO loginDTO);


}
