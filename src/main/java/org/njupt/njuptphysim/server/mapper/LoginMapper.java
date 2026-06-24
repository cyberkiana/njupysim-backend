package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.njupt.njuptphysim.pojo.po.Users;

@Mapper
public interface LoginMapper extends BaseMapper<Users> {

    @Select("select * from njupt_physim.users where account = #{account} and password = #{password}")
    Users getByAccountAndPassword(String account, String password);
}
