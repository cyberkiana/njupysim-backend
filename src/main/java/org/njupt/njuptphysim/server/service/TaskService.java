package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.common.utils.ListPair;
import org.njupt.njuptphysim.pojo.dto.StuCompletedListDTO;
import org.njupt.njuptphysim.pojo.dto.StuFinishedTaskDTO;
import org.njupt.njuptphysim.pojo.dto.StuTaskInfoDTO;
import org.njupt.njuptphysim.pojo.dto.StuUncompletedListDTO;
import org.njupt.njuptphysim.pojo.po.Tasks;
import org.njupt.njuptphysim.pojo.vo.StuTaskInfoVO;
import org.njupt.njuptphysim.pojo.vo.TasksInfoVO;
import org.njupt.njuptphysim.pojo.vo.TasksStatsVO;

import java.util.List;

public interface TaskService {

    /**
     * 查询某学生的所有实验任务
     * @param clazzId 学生所属班级id
     * @return 任务信息
     */
    List<StuTaskInfoDTO> getStuAllTask(String clazzId);

    /**
     * 查询某学生已完成的实验任务
     * @param stuId 学生id
     * @return 任务信息
     */
    List<StuFinishedTaskDTO> getStuFinishedTask(String stuId);


    /**
     * controller入口
     * @param stuId 学生id
     * @return 返回两个列表
     */
    StuTaskInfoVO<StuFinishedTaskDTO,StuTaskInfoDTO> getStuBothExpList(String stuId);

    /**
     * 查询教师发布的所有实验任务
     * @param teaId 教师id
     * @return 实验信息
     */
    List<TasksStatsVO> getTeaTasks(String teaId);

    /**
     * 教师发布新实验任务
     * @param task 任务详情
     * @return 1
     */
    int addNewTask(Tasks task);

    /**
     * 删除实验任务
     * @param teaId 教师id
     * @param taskId 实验任务id
     * @return 1
     */
    int deleteTask(String teaId, int taskId);

    /**
     * 教师获取完成和未完成实验学生名单
     * @param teaId 教师id
     * @param taskId 任务id
     * @param clazzId 班级id
     * @return 完成和未完成实验学生名单
     */
    TasksInfoVO<StuCompletedListDTO, StuUncompletedListDTO> getBothStuList(String teaId, int taskId, String clazzId);

    /**
     * 获取实验任务的具体数据
     * @param teaId 教师id
     * @param clazzId 班级id
     * @param expId 实验id
     * @return
     */
    Tasks getExperimentDetail(String teaId, String clazzId, int expId);

}
