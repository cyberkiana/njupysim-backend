package org.njupt.njuptphysim.server.service;


import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Users;

import java.time.LocalDate;
import java.util.List;


public interface UserService {
    java.util.List<org.njupt.njuptphysim.pojo.vo.UserVO> getByPage(String id, String name, Integer roleId, int offset, int pageSize);

    /**
     * 重置用户密码为初始密码123456
     */
    void resetPassword(String id);

    /**
     * 删除用户头像
     */
    void clearAvatar(String id);

    /**
     * 修改自己的密码：目标用户由登录令牌决定，先校验原密码
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(String oldPassword, String newPassword);

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
     * 更新用户头像url
     * @param id 用户id
     * @param avatar 头像url
     */
    void updateAvatar(String id, String avatar);

    /**
     * 根据id查询角色id
     * @param id 用户id
     * @return 角色id
     */
    int getRoleIdById(String id);

}
