package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.njupt.njuptphysim.pojo.po.IpBlacklist;

import java.util.List;

/**
 * IP黑名单 Mapper（sys_ip_blacklist）
 */
@Mapper
public interface IpBlacklistMapper {

    /**
     * 查询所有处于封禁状态的黑名单规则（供 IpFilter 匹配）
     */
    @Select("select ip from njupt_physim.sys_ip_blacklist where status = 1")
    List<String> searchActiveIps();

    /**
     * 查询全部黑名单记录（封禁中 + 已解封，供管理员查看）
     */
    @Select("select * from njupt_physim.sys_ip_blacklist order by status desc, update_date desc")
    List<IpBlacklist> searchAll();

    /**
     * 新增封禁记录
     */
    @Insert("insert into njupt_physim.sys_ip_blacklist (ip, reason, status, ban_time, operator, create_date, update_date) " +
            "values (#{ip}, #{reason}, 1, NOW(), #{operator}, NOW(), NOW())")
    void insert(@Param("ip") String ip, @Param("reason") String reason, @Param("operator") String operator);

    /**
     * 解封：置状态为0并记录解封时间
     */
    @Update("update njupt_physim.sys_ip_blacklist set status = 0, unban_time = NOW(), " +
            "operator = #{operator}, update_date = NOW() where id = #{id}")
    int unbanById(@Param("id") long id, @Param("operator") String operator);

    /**
     * 对已解封/已存在的同IP记录重新封禁
     */
    @Update("update njupt_physim.sys_ip_blacklist set status = 1, ban_time = NOW(), " +
            "reason = #{reason}, operator = #{operator}, update_date = NOW() where ip = #{ip}")
    int rebanByIp(@Param("ip") String ip, @Param("reason") String reason, @Param("operator") String operator);
}
