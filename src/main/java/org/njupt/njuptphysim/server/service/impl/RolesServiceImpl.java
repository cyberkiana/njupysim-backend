package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.server.mapper.RolesMapper;
import org.njupt.njuptphysim.server.mapper.UserClazzMapper;
import org.njupt.njuptphysim.server.service.RolesService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesMapper rolesMapper;

    @Autowired
    private UserService userService;


    /**
     * 通过userId查询角色name
     * @param userId 用户id
     */
    @Override
    public String getRoleNameByUserId(String userId) {
        String roleName = rolesMapper.getRoleNameById(userService.getRoleIdById(userId));
        return roleName;
    }

    @Override
    public String getRoleNameByRoleId(Integer roleId) {
        return rolesMapper.getRoleNameById(roleId);
    }
}
