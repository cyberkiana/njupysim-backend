package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.njupt.njuptphysim.pojo.dto.ExpListDTO;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.pojo.vo.ExpVO;

import java.util.List;

@Mapper
public interface ResourcesMapper extends BaseMapper<Resources> {

    @Select("select * from njupt_physim.resources where title = #{name}")
    Resources getResourcesByName(String name);

    @Delete("delete from njupt_physim.resources where title = #{name}")
    void deleteByName(String name);

    /**
     * 根据id查实验详情
     * @param id 实验id
     * @return id title createTime createrId url tag content likes easyCount hardCount
     */
    @Select("select * from njupt_physim.resources where id=#{id}")
    Resources getResourcesById(int id);

    @Select("select id as expId,title from njupt_physim.resources")
    List<ExpListDTO> getAllExpList();

    /**
     * 查找班级布置的实验
     * @param clazzId 班级id
     * @return id, title
     */
    List<ExpListDTO> getClazzExpList(String clazzId);

    /**
     * 点赞、十分简单、十分困难评价计数+1
     * @param id 实验id
     * @param likes 点赞
     * @param easyCount 十分简单
     * @param hardCount 十分困难
     */
    void updateEvaluation(int id, Integer likes, Integer easyCount, Integer hardCount);

}
