package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.njupt.njuptphysim.pojo.po.UserClass;


@Mapper
public interface UserClazzMapper extends BaseMapper<UserClass> {

    /**
     * 删除用户的所有班级关联（删除用户前必须先调用，否则外键约束失败）
     * @param userId 用户id
     * @return 删除的关联行数
     */
    @Delete("delete from njupt_physim.user_clazz where user_id = #{userId}")
    int deleteByUserId(String userId);

    /**
     * 删除班级的全部剩余关联（删除班级时使用，调用方需先确认无学生）
     * @param clazzId 班级id
     * @return 删除的关联行数
     */
    @Delete("delete from njupt_physim.user_clazz where clazz_id = #{clazzId}")
    int deleteByClazzId(String clazzId);
}
