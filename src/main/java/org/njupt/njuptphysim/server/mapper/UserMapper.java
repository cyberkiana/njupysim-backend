package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Users;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<Users> {

    void addUserToClazz(String clazzId, String userId, int roleId);

    int getTotalNum(String id, String name, Integer roleId);

    List<Users> getAllByPage(String id, String name, Integer roleId, int offset, int pageSize);

    void editUserById(String id, String name, Integer roleId, String account, String password, LocalDate createTime);

    UserDTO searchSecurityInfoByName(String name);

    UserDTO searchBaseInfoById(String id);




}
