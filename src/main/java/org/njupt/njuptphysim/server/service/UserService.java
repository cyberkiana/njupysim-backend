package org.njupt.njuptphysim.server.service;


import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Users;

import java.time.LocalDate;
import java.util.List;


public interface UserService {
    List<Users> getByPage(String id, String name, Integer roleId, int offset, int pageSize);

    int getTotalNum(String id, String name, Integer roleId);

    void addUser(Users user);

    void deleteUser(String id);

    void editUser(String id, String name, Integer roleId, String account, String password, LocalDate createTime);

    UserDTO searchUserInfoByName(String name);

    /**
     * 根据id查询姓名
     * @param id 用户id
     * @return 用户姓名
     */
    String getNameById(String id);

    /**
     * 管理员查询某时段预约学生信息
     * @param id 学生id
     * @return 基本信息
     */
    UserDTO searchBaseInfoById(String id);

    /**
     * 根据id查询头像url
     * @param id 用户id
     * @return 头像url
     */
    String getAvatarById(String id);

    /**
     * 根据id查询角色id
     * @param id 用户id
     * @return 角色id
     */
    int getRoleIdById(String id);

}
