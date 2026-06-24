package org.njupt.njuptphysim.server.controller.common;


import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.dto.LoginDTO;
import org.njupt.njuptphysim.pojo.vo.LoginVO;
import org.njupt.njuptphysim.server.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class LoginController {

    @Autowired
    private LoginService userService;

    /**
     * 登录
     * @param loginDTO
     * @return loginVO
     */
    @PostMapping("/login")
    public Result login( @RequestBody LoginDTO loginDTO){
        LoginVO loginVO= userService.login(loginDTO);
        return Result.success(loginVO);
    }

}
