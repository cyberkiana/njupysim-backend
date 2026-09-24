package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.po.IpBlacklist;
import org.njupt.njuptphysim.server.service.IpBlacklistService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * IP黑名单管理（数据库表 sys_ip_blacklist）：管理员可查看黑名单并解封
 */
@RestController
@RequestMapping("/admin/ipblacklist")
public class IpBlacklistController {

    @Autowired
    private IpBlacklistService ipBlacklistService;

    @Autowired
    private UserService userService;

    /**
     * 查看全部黑名单记录（含已解封）
     */
    @OperationLog(value = "查询", detail = "查看IP黑名单列表")
    @GetMapping("")
    public Result listAll() {
        List<IpBlacklist> list = ipBlacklistService.listAll();
        return Result.success(Map.of("list", list, "total", list.size()));
    }

    /**
     * 解封黑名单记录
     * @param id 黑名单记录id
     */
    @OperationLog(value = "解封", detail = "解封IP黑名单记录")
    @PostMapping("/{id}/unban")
    public Result unban(@PathVariable long id) {
        ipBlacklistService.unban(id, currentOperatorName());
        return Result.success();
    }

    /**
     * 手动封禁IP
     * @param body { ip: 封禁的IP规则, reason: 封禁原因 }
     */
    @OperationLog(value = "封禁", detail = "手动封禁IP")
    @PostMapping("")
    public Result ban(@RequestBody Map<String, String> body) {
        String ip = body.get("ip");
        if (ip == null || ip.isBlank()) {
            return Result.error("IP不能为空");
        }
        String reason = body.getOrDefault("reason", "管理员手动封禁");
        ipBlacklistService.ban(ip.trim(), reason, currentOperatorName());
        return Result.success();
    }

    /**
     * 获取当前管理员的姓名（作为黑名单操作人记录）
     */
    private String currentOperatorName() {
        String userId = BaseContext.getCurrentId();
        if (userId == null || userId.isEmpty()) {
            return "admin";
        }
        try {
            String name = userService.getNameById(userId);
            return (name == null || name.isEmpty()) ? userId : name;
        } catch (Exception e) {
            return userId;
        }
    }
}
