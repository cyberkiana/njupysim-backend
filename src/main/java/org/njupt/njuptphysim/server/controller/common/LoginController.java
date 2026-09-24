package org.njupt.njuptphysim.server.controller.common;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.utils.IpUtil;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.dto.LoginDTO;
import org.njupt.njuptphysim.pojo.vo.LoginVO;
import org.njupt.njuptphysim.server.service.LoginLimitService;
import org.njupt.njuptphysim.server.service.LoginService;
import org.njupt.njuptphysim.server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录/退出。
 *
 * <p>登录、退出属于认证行为，不使用 @OperationLog 注解，
 * 登录日志直接在方法内持久化到 sys_log_login 并输出控制台日志。</p>
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService userService;

    @Autowired
    private LoginLimitService loginLimitService;

    @Autowired
    private LogService logService;

    /**
     * 登录（登录前先做 Redis 登录计数限流，1 分钟超过 30 次封禁 IP）
     * @param loginDTO 账号密码
     * @return loginVO
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ip = IpUtil.getClientIp(request);
        if (loginLimitService.isBlockedAfterRecord(ip)) {
            // 触发封禁的这一次尝试同样记录登录日志（失败）
            logService.saveLoginLog(0, 0, loginDTO.getAccount());
            return Result.error("登录过于频繁，IP已被封禁，请联系管理员解封");
        }
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    /**
     * 退出登录，记录退出日志（sys_log_login operation=1）
     */
    @PostMapping("/logout")
    public Result logout() {
        userService.logout();
        return Result.success();
    }

}
