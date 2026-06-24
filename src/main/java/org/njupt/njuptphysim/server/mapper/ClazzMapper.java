package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.njupt.njuptphysim.pojo.dto.ClazzListDTO;
import org.njupt.njuptphysim.pojo.po.Clazzes;
import org.njupt.njuptphysim.pojo.po.Users;

import java.util.List;

@Mapper
public interface ClazzMapper extends BaseMapper<Clazzes> {

    @Update("update njupt_physim.user_clazz set user_id = #{teacherId} where clazz_id = #{id} and user_id = #{teacherId}")
    void updateTeacher(String clazzId, String teacherId);

    /**
     * 分页查询
     * @param id
     * @param createTime
     * @param offset
     * @param pageSize
     * @return
     */
    //@Select("select * from njupt_physim.classes limit #{offset},#{pageSize}")
    List<Clazzes> getAllByPage(@Param("id") String id, @Param("createTime") String createTime, @Param("offset") int offset, @Param("pageSize") int pageSize);


    /**
     * 查询本次查询的总条目数
     * @return 查询的总条目数
     */
    int getTotalNum(String id, String createTime, String teacher);

    /**
     * 查询某班级的学生信息
     * @param clazzId
     * @return
     */
    List<Users> getStuFromClazz(String clazzId);

    /**
     * 从班级内删除学生
     * @param clazzId 班级id
     * @param stuId 学生id
     */
    void deleteStuFromClazz(String clazzId, String stuId);

    /**
     * 获取学生的班级
     * @param stuId 学生id
     * @return 班级id
     */
    @Select("select clazz_id from njupt_physim.user_clazz where user_id=#{stuId}")
    String getClazzOfStu(String stuId);

    /**
     * 获取班级学生数
     * @param clazzId 班级id
     * @return 学生数
     */
    @Select("select count(*) from njupt_physim.user_clazz where clazz_id=#{clazzId} and role_id=3")
    int getClazzStuNum(String clazzId);

    /**
     * 获取教师教授的班级id
     * @param teaId 教师id
     * @return List<ClazzListDTO>
     */
    List<ClazzListDTO> getTeaClazzList(String teaId);

    /**
     * 获取班级内所有学生信息
     * @param clazzId 班级id
     * @return List<Users>
     */
    @Select("select * from njupt_physim.users where id in (select user_id from njupt_physim.user_clazz where clazz_id=#{clazzId} and user_clazz.role_id=3 )")
    List<Users> getStusOfClazz(String clazzId);

    /**
     * 根据班级id获取任课老师姓名
     * @param clazzId 班级id
     * @return 老师姓名
     */
    String getTeacherOfClazz(String clazzId);

}
