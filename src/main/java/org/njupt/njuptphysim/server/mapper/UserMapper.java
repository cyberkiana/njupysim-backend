package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Users;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<Users> {

    void addUserToClazz(String clazzId, String userId, int roleId);

    /**
     * 按用户id更新密码（管理员重置初始密码 / 用户修改自己密码共用）
     */
    @org.apache.ibatis.annotations.Update("update njupt_physim.users set password = #{password} where id = #{id}")
    int updatePasswordById(@org.apache.ibatis.annotations.Param("id") String id, @org.apache.ibatis.annotations.Param("password") String password);

    /**
     * 管理员删除用户头像(置空)
     */
    @org.apache.ibatis.annotations.Update("update njupt_physim.users set avatar = '' where id = #{id}")
    int clearAvatar(@org.apache.ibatis.annotations.Param("id") String id);

    int getTotalNum(String id, String name, Integer roleId);

    List<org.njupt.njuptphysim.pojo.vo.UserVO> getAllByPage(String id, String name, Integer roleId, int offset, int pageSize);

    void editUserById(String id, String name, Integer roleId, String account, String password, LocalDate createTime);

    UserDTO searchSecurityInfoByName(String name);

    UserDTO searchBaseInfoById(String id);

    /**
     * 更新用户头像url
     * @param id 用户id
     * @param avatar 头像url（nginx静态路径，如 /avatar/B22060101_1694567890.jpg）
     */
    void updateAvatarById(@Param("id") String id, @Param("avatar") String avatar);

}
