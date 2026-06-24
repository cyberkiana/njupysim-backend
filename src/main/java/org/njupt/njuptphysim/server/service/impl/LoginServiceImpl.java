package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.common.properties.JwtProperty;
import org.njupt.njuptphysim.common.utils.JwtUtil;
import org.njupt.njuptphysim.pojo.dto.LoginDTO;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.pojo.vo.LoginVO;
import org.njupt.njuptphysim.server.mapper.RolesMapper;
import org.njupt.njuptphysim.server.mapper.UserClazzMapper;
import org.njupt.njuptphysim.server.mapper.LoginMapper;
import org.njupt.njuptphysim.server.service.LoginService;
import org.njupt.njuptphysim.server.service.RolesService;
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

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        Users users = usersMapper.getByAccountAndPassword(loginDTO.getAccount(),loginDTO.getPassword());
        //判断账号密码是否正确
        if(users == null){
            throw new RuntimeException("账号密码错误");
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

        //封装成VO返回
        return LoginVO.builder()
                .id(users.getId())
                .name(users.getName())
                .account(users.getAccount())
                .token(token)
                .roleName(roleName)
                .build();
    }


}
