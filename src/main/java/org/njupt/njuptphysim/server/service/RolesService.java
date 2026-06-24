package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.server.mapper.RolesMapper;
import org.springframework.beans.factory.annotation.Autowired;

public interface RolesService {


    /**
     * 通过userId查询角色name
     * @param userId
     */
    String getRoleNameByUserId(String userId);

    String getRoleNameByRoleId(Integer roleId);

}
