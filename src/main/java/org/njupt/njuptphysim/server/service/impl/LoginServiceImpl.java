package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.common.properties.JwtProperty;
import org.njupt.njuptphysim.common.utils.JwtUtil;
import org.njupt.njuptphysim.pojo.dto.LoginDTO;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.pojo.vo.LoginVO;
import org.njupt.njuptphysim.server.mapper.LoginMapper;
import org.njupt.njuptphysim.server.service.LoginService;
import org.njupt.njuptphysim.server.service.LogService;
import org.njupt.njuptphysim.server.service.RolesService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginMapper usersMapper;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private LogService logService;
    @Autowired
    private UserService userService;
    @Autowired
    private org.njupt.njuptphysim.server.service.UserBanService userBanService;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        Users users = usersMapper.getByAccountAndPassword(loginDTO.getAccount(),loginDTO.getPassword());
        //判断账号密码是否正确
        if(users == null){
            //登录失败，直接在方法内持久化登录日志并输出控制台日志
            logService.saveLoginLog(0, 0, loginDTO.getAccount());
            throw new BaseException("账号密码错误");
        }

        //判断账号是否已被封禁（封禁账号无法登录）
        if (userBanService.isUserBanned(users.getId())) {
            logService.saveLoginLog(0, 0, loginDTO.getAccount());
            throw new BaseException("该账号已被封禁，请联系管理员");
        }

        //获取用户角色
        int roleId = users.getRoleId();
        String roleName = rolesService.getRoleNameByRoleId(roleId);
        //获取token
        Map<String,Object> claims = new HashMap<>();
        claims.put("id",users.getId());
        String token = JwtUtil.createJWT(
                jwtProperty.getSecretKey(),
                jwtProperty.getTtlMillis(),
                claims
        );

        //登录成功，持久化登录日志（sys_log_login operation=0 status=1）
        logService.saveLoginLog(0, 1, users.getAccount());

        //封装成VO返回
        return LoginVO.builder()
                .id(users.getId())
                .name(users.getName())
                .account(users.getAccount())
                .token(token)
                .roleName(roleName)
                .build();
    }

    @Override
    public void logout() {
        //退出登录，持久化登录日志（sys_log_login operation=1 status=1）
        String userId = BaseContext.getCurrentId();
        String userName = userId;
        try {
            String name = userService.getNameById(userId);
            if (name != null && !name.isEmpty()) {
                userName = name;
            }
        } catch (Exception ignored) {
        }
        logService.saveLoginLog(1, 1, userName);
    }
}
