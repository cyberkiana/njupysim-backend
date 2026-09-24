package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 实验评价记录 Mapper：每个用户对每个实验仅能评价一次（点赞/十分简单/十分困难三选一）
 */
@Mapper
public interface ExpEvaluationMapper {

    /**
     * 查询用户对某实验的评价类型
     * @param userId 用户id
     * @param resourceId 实验id
     * @return 类型(1点赞 2十分简单 3十分困难)，未评价时为 null
     */
    @Select("select type from njupt_physim.exp_evaluation_record where user_id = #{userId} and resource_id = #{resourceId}")
    Integer getType(@Param("userId") String userId, @Param("resourceId") int resourceId);

    /**
     * 写入评价记录
     */
    @Insert("insert into njupt_physim.exp_evaluation_record (user_id, resource_id, type, create_date) " +
            "values (#{userId}, #{resourceId}, #{type}, NOW())")
    void insert(@Param("userId") String userId, @Param("resourceId") int resourceId, @Param("type") int type);
}
