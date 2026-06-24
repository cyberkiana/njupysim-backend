package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.njupt.njuptphysim.pojo.po.Roles;


@Mapper
public interface RolesMapper extends BaseMapper<Roles> {


    @Select("select name from njupt_physim.roles where id = #{roleId}")
    String getRoleNameById(int roleId);
}
