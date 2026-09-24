package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.njupt.njuptphysim.pojo.dto.StuCompletedListDTO;
import org.njupt.njuptphysim.pojo.dto.StuTaskInfoDTO;
import org.njupt.njuptphysim.pojo.po.Completions;
import org.njupt.njuptphysim.pojo.po.Tasks;
import org.njupt.njuptphysim.pojo.vo.TasksStatsVO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TaskMapper {

    /**
     * 查询学生所在班级的所有实验任务
     * @param clazzId 班级id
     * @return 任务信息
     */
    @Select("select id,teacher,exp_id,start_date,end_date from njupt_physim.tasks where clazz_id=#{clazzId}")
    List<Tasks> getStuAllTask(String clazzId);

    /**
     * 查询学生所有已完成实验任务
     * @param stuId 学生id
     * @return 任务信息
     */
    @Select("select * from njupt_physim.completions where stu_id=#{stuId}")
    List<Completions> getStuAllFinishedTask(String stuId);

    /**
     * 根据任务id查对应实验id
     * @param id taskId
     * @return 实验id；任务不存在时为 null
     */
    @Select("select exp_id from njupt_physim.tasks where id=#{id}")
    Integer getExpIdById(int id);

    /**
     * 按任务id查询任务详情（唯一，避免同班级同实验多任务时按实验id查询的歧义）
     * @param taskId 任务id
     * @return 任务详情，不存在时为 null
     */
    @Select("select id, teacher, clazz_id, exp_id, start_date, end_date, complete_count, total_count "
            + "from njupt_physim.tasks where id = #{taskId}")
    Tasks getTaskById(int taskId);

    /**
     * 根据任务id查教师姓名
     * @param id taskId
     * @return 教师姓名
     */
    @Select("select teacher from njupt_physim.tasks where id=#{id}")
    String getTeacherById(int id);

    /**
     * 统计学生是否已完成某任务（防重复记录）
     */
    @Select("select count(*) from njupt_physim.completions where stu_id = #{stuId} and task_id = #{taskId}")
    int countCompletion(@Param("stuId") String stuId, @Param("taskId") int taskId);

    /**
     * 写入完成记录
     */
    @Insert("insert into njupt_physim.completions (stu_id, clazz_id, task_id, time, score) " +
            "values (#{stuId}, #{clazzId}, #{taskId}, NOW(), #{score})")
    void insertCompletion(@Param("stuId") String stuId, @Param("clazzId") String clazzId,
                          @Param("taskId") int taskId, @Param("score") int score);

    /**
     * 实时统计任务完成人数（以completions表为准，不读tasks表的快照字段）
     */
    @Select("select count(*) from njupt_physim.completions where task_id = #{taskId}")
    int countTaskCompletions(int taskId);

    /**
     * 根据教师姓名查询发布的任务（完成数/总人数实时统计，与completions表和班级名单同口径）
     * @param teacher 教师姓名
     * @return 任务列表
     */
    @Select("select t.id, t.clazz_id, t.exp_id, t.start_date, t.end_date, "
            + "(select count(*) from njupt_physim.completions c where c.task_id = t.id) as complete_count, "
            + "(select count(*) from njupt_physim.user_clazz uc where uc.clazz_id = t.clazz_id and uc.role_id = 3) as total_count "
            + "from njupt_physim.tasks t where t.teacher = #{teacher}")
    List<TasksStatsVO> getTeaTasks(String teacher);

    /**
     * 教师新增实验任务
     * @param teacher 教师姓名
     * @param clazzId 班级id
     * @param expId   实验id
     * @param startDate 开始日期
     * @param endDate  截止日期
     * @param totalCount 班级人数
     */
    void addNewTask(String teacher, String clazzId, int expId, LocalDate startDate, LocalDate endDate, int totalCount);

    /**
     * 删除实验任务
     * @param taskId 实验任务id
     */
    @Delete("delete from njupt_physim.tasks where id=#{taskId}")
    void deleteTask(int taskId);

    /**
     * 删除任务下的全部完成记录（删任务时一并清理，避免孤儿数据）
     * @param taskId 实验任务id
     */
    @Delete("delete from njupt_physim.completions where task_id=#{taskId}")
    void deleteCompletionsByTaskId(int taskId);

    /**
     * 查询某实验任务某班级已完成学生名单
     * @param taskId 任务id
     * @param clazzId 班级id
     * @return
     */
    List<StuCompletedListDTO> getCompletedExpStuList(int taskId, String clazzId);

}
