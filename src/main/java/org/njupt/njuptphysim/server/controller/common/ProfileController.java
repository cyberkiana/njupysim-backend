package org.njupt.njuptphysim.server.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.dto.ChangePasswordDTO;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人中心：当前登录用户的自助操作。
 *
 * <p>所有接口的目标用户均取自登录令牌解析出的 BaseContext 当前用户id，
 * 请求体中不接收、也不信任任何用户id参数，从入口上杜绝修改他人数据。</p>
 */
@RestController
@RequestMapping("/common/user")
@Slf4j
public class ProfileController {

    @Autowired
    private UserService userService;

    /**
     * 修改自己的密码：校验原密码无误后用新密码覆盖
     * @param dto { oldPassword, newPassword }
     */
    @OperationLog(value = "修改", detail = "修改自己的密码")
    @PostMapping("/password")
    public Result changePassword(@RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }
}
