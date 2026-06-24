package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.server.mapper.UserMapper;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Users> getByPage(String id, String name, Integer roleId, int offset, int pageSize) {
        return userMapper.getAllByPage(id, name, roleId, offset, pageSize);
    }

    @Override
    public int getTotalNum(String id, String name, Integer roleId) {
        return userMapper.getTotalNum(id, name, roleId);
    }

    @Override
    public void editUser(String id, String name, Integer roleId, String account, String password, LocalDate createTime) {
        userMapper.editUserById(id, name, roleId, account, password, createTime);
    }

    @Override
    public UserDTO searchUserInfoByName(String name) {
        return userMapper.searchSecurityInfoByName(name);
    }

    @Override
    public String getNameById(String id) {
        return userMapper.selectById(id).getName();
    }

    @Override
    public UserDTO searchBaseInfoById(String id) {
        return userMapper.searchBaseInfoById(id);
    }

    @Override
    public String getAvatarById(String id) {
        return userMapper.selectById(id).getAvatar();
    }

    @Override
    public int getRoleIdById(String id) {
        return userMapper.selectById(id).getRoleId();
    }

    @Override
    public void addUser(Users user) {
        userMapper.insert(user);
        // 新用户顺手加入班级
        if(user.getRoleId()==3){
            String userId = user.getId();
            String clazzId = userId.substring(0,7);
            userMapper.addUserToClazz(clazzId, userId, 3);
        }else if(user.getRoleId()==2){
            userMapper.addUserToClazz("TEACHER", user.getId(), 2);
        }else {
            userMapper.addUserToClazz("ADMIN", user.getId(), 1);
        }
    }

    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(id);
    }
}
