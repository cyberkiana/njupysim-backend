package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.server.service.UserBanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员账号封禁管理：封禁后账号无法登录，已登录的请求也会被拦截
 */
@RestController
@RequestMapping("/admin/userbans")
public class UserBanController {

    @Autowired
    private UserBanService userBanService;


    @Autowired
    private org.njupt.njuptphysim.server.service.RolesService rolesService;

    /**
     * 查询全部封禁记录（封禁中+已解封）
     */
    @OperationLog(value = "查询", detail = "查看账号封禁记录")
    @GetMapping
    public Result listAll() {
        return Result.success(userBanService.listAll());
    }

    /**
     * 封禁账号
     * @param body { userId: 账号id, reason: 封禁原因(可选) }
     */
    @OperationLog(value = "新增", detail = "封禁账号")
    @PostMapping
    public Result ban(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String reason = body.getOrDefault("reason", "");
        if (userId == null || userId.isBlank()) {
            throw new BaseException("请指定要封禁的账号");
        }
        if (userId.equals(BaseContext.getCurrentId())) {
            throw new BaseException("不能封禁当前登录的管理员自己");
        }
        if (userBanService.isUserBanned(userId)) {
            throw new BaseException("该账号已在封禁中");
        }
        //管理员账号不允许封禁
        String roleName;
        try {
            roleName = rolesService.getRoleNameByUserId(userId);
        } catch (Exception e) {
            throw new BaseException("要封禁的账号不存在");
        }
        if ("admin".equals(roleName)) {
            throw new BaseException("不能封禁管理员账号");
        }
        userBanService.ban(userId, reason, BaseContext.getCurrentId());
        return Result.success();
    }

    /**
     * 解封账号
     * @param id 封禁记录id
     */
    @OperationLog(value = "修改", detail = "解封账号")
    @PostMapping("/{id}/unban")
    public Result unban(@PathVariable long id) {
        userBanService.unban(id, BaseContext.getCurrentId());
        return Result.success();
    }
}
