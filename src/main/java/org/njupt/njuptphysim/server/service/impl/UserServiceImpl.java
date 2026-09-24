package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Clazzes;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.server.mapper.ClazzMapper;
import org.njupt.njuptphysim.server.mapper.UserClazzMapper;
import org.njupt.njuptphysim.server.mapper.UserMapper;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserClazzMapper userClazzMapper;

    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public List<org.njupt.njuptphysim.pojo.vo.UserVO> getByPage(String id, String name, Integer roleId, int offset, int pageSize) {
        return userMapper.getAllByPage(id, name, roleId, offset, pageSize);
    }

    /** 初始密码, 与前端提示保持一致 */
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void resetPassword(String id) {
        if (userMapper.selectById(id) == null) {
            throw new BaseException("用户不存在");
        }
        userMapper.updatePasswordById(id, DEFAULT_PASSWORD);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // 目标用户只从登录令牌解析，不接受前端传入id，保证只能修改自己的密码
        String id = BaseContext.getCurrentId();
        Users user = userMapper.selectById(id);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new BaseException("请输入原密码");
        }
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 32) {
            throw new BaseException("新密码长度需为6-32位");
        }
        if (newPassword.equals(oldPassword)) {
            throw new BaseException("新密码不能与原密码相同");
        }
        // 原密码核验通过才允许覆盖，防止token被盗后无凭据改密
        if (!oldPassword.equals(user.getPassword())) {
            throw new BaseException("原密码错误");
        }
        userMapper.updatePasswordById(id, newPassword);
    }

    @Override
    public void clearAvatar(String id) {
        if (userMapper.selectById(id) == null) {
            throw new BaseException("用户不存在");
        }
        userMapper.clearAvatar(id);
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
    public void updateAvatar(String id, String avatar) {
        userMapper.updateAvatarById(id, avatar);
    }

    @Override
    public int getRoleIdById(String id) {
        return userMapper.selectById(id).getRoleId();
    }

    @Override
    public void addUser(Users user) {
        //create_time、avatar、college 列 NOT NULL，前端未传时填默认值
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDate.now());
        }
        if (user.getAvatar() == null || user.getAvatar().isBlank()) {
            user.setAvatar("");
        }
        if (user.getCollege() == null || user.getCollege().isBlank()) {
            user.setCollege("");
        }
        userMapper.insert(user);
        // 新用户顺手加入班级（roles 表与 user_clazz.role_id 约定一致：1管理员/2教师/3学生）
        if(user.getRoleId()==3){
            String userId = user.getId();
            String clazzId = userId.length() >= 7 ? userId.substring(0,7) : userId;
            //clazz_id 有外键指向 clazzes 表，班级不存在时先自动建班
            if (clazzMapper.selectById(clazzId) == null) {
                clazzMapper.insert(Clazzes.builder().id(clazzId).createTime(Year.now()).build());
            }
            userMapper.addUserToClazz(clazzId, userId, 3);
        }else if(user.getRoleId()==2){
            userMapper.addUserToClazz("TEACHER", user.getId(), 2);
        }else {
            userMapper.addUserToClazz("ADMIN", user.getId(), 1);
        }
    }

    @Override
    public void deleteUser(String id) {
        //先清理 user_clazz 关联，否则外键约束导致删除失败
        userClazzMapper.deleteByUserId(id);
        userMapper.deleteById(id);
    }
}
