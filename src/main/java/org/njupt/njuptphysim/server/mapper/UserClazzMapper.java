package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.njupt.njuptphysim.pojo.po.UserClass;

import java.util.List;

@Mapper
public interface UserClazzMapper extends BaseMapper<UserClass> {

    /**
     * 通过班级id获取所有用户id
     * @param id
     * @return 用户id列表
     */
    @Select("select user_id from njupt_physim.user_clazz where clazz_id = #{id}")
    List<String> getAllUserIdByClazzId(String id);

    /**
     * 通过用户id获取角色id
     * @param id
     * @return 角色id
     */
    @Select("select role_id from njupt_physim.user_clazz where user_id = #{id}")
    int getRoleIdByUserId(String id);
}
