package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.njupt.njuptphysim.pojo.vo.UserBan;

import java.util.List;

/**
 * 账号封禁 Mapper（sys_user_ban）
 */
@Mapper
public interface UserBanMapper {

    /**
     * 查询全部封禁记录（封禁中 + 已解封，供管理员查看）
     */
    @Select("select * from njupt_physim.sys_user_ban order by status desc, update_date desc")
    List<UserBan> searchAll();

    /**
     * 根据id查询记录
     */
    @Select("select * from njupt_physim.sys_user_ban where id = #{id}")
    UserBan searchById(@Param("id") long id);

    /**
     * 查询某账号处于封禁状态的记录条数
     */
    @Select("select count(*) from njupt_physim.sys_user_ban where user_id = #{userId} and status = 1")
    int countActiveByUserId(@Param("userId") String userId);

    /**
     * 查询全部处于封禁状态的账号（供登录/请求拦截匹配）
     */
    @Select("select user_id from njupt_physim.sys_user_ban where status = 1")
    List<String> searchActiveUserIds();

    /**
     * 新增封禁记录
     */
    @Insert("insert into njupt_physim.sys_user_ban (user_id, reason, status, ban_time, operator, create_date, update_date) " +
            "values (#{userId}, #{reason}, 1, NOW(), #{operator}, NOW(), NOW())")
    void insert(@Param("userId") String userId, @Param("reason") String reason, @Param("operator") String operator);

    /**
     * 解封：置状态为0并记录解封时间
     */
    @Update("update njupt_physim.sys_user_ban set status = 0, unban_time = NOW(), update_date = NOW() where id = #{id}")
    int unbanById(@Param("id") long id);

    /**
     * 对已解封/已存在的同账号记录重新封禁
     */
    @Update("update njupt_physim.sys_user_ban set status = 1, ban_time = NOW(), reason = #{reason}, " +
            "operator = #{operator}, update_date = NOW() where user_id = #{userId}")
    int rebanByUserId(@Param("userId") String userId, @Param("reason") String reason, @Param("operator") String operator);
}
