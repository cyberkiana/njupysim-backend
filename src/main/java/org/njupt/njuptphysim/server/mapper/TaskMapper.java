package org.njupt.njuptphysim.server.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
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
     * @return 实验id
     */
    @Select("select exp_id from njupt_physim.tasks where id=#{id}")
    int getExpIdById(int id);

    /**
     * 根据任务id查教师姓名
     * @param id taskId
     * @return 教师姓名
     */
    @Select("select teacher from njupt_physim.tasks where id=#{id}")
    String getTeacherById(int id);

    /**
     * 根据教师姓名查询发布的任务
     * @param teacher 教师姓名
     * @return 任务列表
     */
    @Select("select id,clazz_id,exp_id,start_date,end_date,complete_count,total_count from njupt_physim.tasks where teacher=#{teacher}")
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
     * 查询某实验任务某班级已完成学生名单
     * @param taskId 任务id
     * @param clazzId 班级id
     * @return
     */
    List<StuCompletedListDTO> getCompletedExpStuList(int taskId, String clazzId);

    /**
     * 获取实验任务的具体数据
     * @param teaId 教师id
     * @param clazzId 班级id
     * @param expId 实验id
     * @return
     */
    Tasks getExperimentDetail(String teaId, String clazzId, int expId);

}
