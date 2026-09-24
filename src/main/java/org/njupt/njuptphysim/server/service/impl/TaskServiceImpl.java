package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.dto.StuCompletedListDTO;
import org.njupt.njuptphysim.pojo.dto.StuFinishedTaskDTO;
import org.njupt.njuptphysim.pojo.dto.StuTaskInfoDTO;
import org.njupt.njuptphysim.pojo.dto.StuUncompletedListDTO;
import org.njupt.njuptphysim.pojo.po.Completions;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.pojo.po.Tasks;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.pojo.vo.StuTaskInfoVO;
import org.njupt.njuptphysim.pojo.vo.TasksInfoVO;
import org.njupt.njuptphysim.pojo.vo.TasksStatsVO;
import org.njupt.njuptphysim.server.mapper.TaskMapper;
import org.njupt.njuptphysim.server.service.ClazzService;
import org.njupt.njuptphysim.server.service.ResourcesService;
import org.njupt.njuptphysim.server.service.TaskService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Transactional
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private UserService userService;

    @Override
    public List<StuTaskInfoDTO> getStuAllTask(String stuId) {
        List<StuTaskInfoDTO> list = new ArrayList<>();
        String clazzId = clazzService.getClazzOfStu(stuId);
        List<Tasks> stuAllTask = taskMapper.getStuAllTask(clazzId);
        for (Tasks tasks : stuAllTask) {
            StuTaskInfoDTO stuTaskInfoDTO = new StuTaskInfoDTO();
            stuTaskInfoDTO.setId(tasks.getId());
            stuTaskInfoDTO.setTeacher(tasks.getTeacher());
            stuTaskInfoDTO.setStartDate(tasks.getStartDate());
            stuTaskInfoDTO.setEndDate(tasks.getEndDate());
            Resources resource = resourcesService.getResourcesById(tasks.getExpId());
            //实验资源可能已被删除, 兜底展示, 不让单个任务拖垮整个列表
            stuTaskInfoDTO.setTitle(resource != null ? resource.getTitle() : "未知实验");
            stuTaskInfoDTO.setContent(resource != null ? resource.getContent() : "");
            list.add(stuTaskInfoDTO);
        }
        return list;
    }

    @Override
    public List<StuFinishedTaskDTO> getStuFinishedTask(String stuId) {
        List<StuFinishedTaskDTO> list = new ArrayList<>();
        List<Completions> finishedTask = taskMapper.getStuAllFinishedTask(stuId);
        for (Completions completions : finishedTask) {
            //完成记录引用的任务可能已被删除, 跳过孤儿记录, 避免NPE导致整个列表查询失败
            Integer expId = taskMapper.getExpIdById(completions.getTaskId());
            if (expId == null) {
                continue;
            }
            StuFinishedTaskDTO sft = new StuFinishedTaskDTO();
            sft.setId(completions.getId());
            sft.setScore(completions.getScore());
            sft.setFinishDate(completions.getTime());
            Resources resource = resourcesService.getResourcesById(expId);
            if (resource == null) {
                continue;
            }
            sft.setTitle(resource.getTitle());
            sft.setContent(resource.getContent());
            sft.setTeacher(taskMapper.getTeacherById(completions.getTaskId()));
            list.add(sft);
        }
        return list;
    }

    @Override
    public StuTaskInfoVO<StuFinishedTaskDTO, StuTaskInfoDTO> getStuBothExpList(String stuId) {
        List<StuTaskInfoDTO> stuAllTask = getStuAllTask(stuId);
        List<StuFinishedTaskDTO> stuFinishedTask = getStuFinishedTask(stuId);
        List<StuTaskInfoDTO> stuUnfinishedTask = new ArrayList<>();
        HashSet<Integer> finishedTaskId = new HashSet<>();
        for (StuFinishedTaskDTO dto : stuFinishedTask) {
            finishedTaskId.add(dto.getId());
        }
        for (StuTaskInfoDTO dto : stuAllTask) {
            if (!finishedTaskId.contains(dto.getId())){
                stuUnfinishedTask.add(dto);
            }
        }
        return new StuTaskInfoVO<>(stuFinishedTask,stuUnfinishedTask);
    }

    @Override
    public List<TasksStatsVO> getTeaTasks(String teaId) {
        return taskMapper.getTeaTasks(userService.getNameById(teaId));
    }

    @Override
    public int addNewTask(Tasks task) {
        int clazzStuNum = clazzService.getClazzStuNum(task.getClazzId());
        taskMapper.addNewTask(task.getTeacher(), task.getClazzId(), task.getExpId(),
                task.getStartDate(), task.getEndDate(), clazzStuNum);
        return 1;
    }

    @Override
    public int deleteTask(String teaId, int taskId) {
        //只有任务归属教师（按教师姓名匹配）才能删除
        String taskTeacher = taskMapper.getTeacherById(taskId);
        if (taskTeacher == null) {
            throw new BaseException("任务不存在");
        }
        if (!taskTeacher.equals(userService.getNameById(teaId))) {
            throw new BaseException("无权限删除其他教师的任务");
        }
        taskMapper.deleteTask(taskId);
        //一并清理该任务的完成记录，避免孤儿数据干扰统计
        taskMapper.deleteCompletionsByTaskId(taskId);
        return 1;
    }

    @Override
    public TasksInfoVO<StuCompletedListDTO, StuUncompletedListDTO> getBothStuList(String teaId, int taskId, String clazzId) {
        List<StuCompletedListDTO> completedList = taskMapper.getCompletedExpStuList(taskId, clazzId);
        List<StuUncompletedListDTO> uncompletedList = new ArrayList<>();
        HashSet<String> completedStuIdList = new HashSet<>();
        for (StuCompletedListDTO dto : completedList) {
            completedStuIdList.add(dto.getStuId());
        }
        for (org.njupt.njuptphysim.pojo.vo.UserVO stuOfClazz : clazzService.getStusOfClazz(clazzId)) {
            if (!completedStuIdList.contains(stuOfClazz.getId())) {
                uncompletedList.add(new StuUncompletedListDTO(stuOfClazz.getId(), stuOfClazz.getName()));
            }
        }
        return new TasksInfoVO<>(completedList, uncompletedList);
    }

    @Override
    public Tasks completeTask(String stuId, int expId, Integer score) {
        String clazzId = clazzService.getClazzOfStu(stuId);
        if (clazzId == null) {
            throw new BaseException("未找到学生所在班级");
        }
        int s = (score == null) ? 100 : score;
        if (s < 0 || s > 100) {
            throw new BaseException("成绩应在0~100之间");
        }
        //找该班级下同实验、该学生尚未完成的任务, 按截止日期最早优先
        List<Tasks> candidates = new ArrayList<>();
        for (Tasks t : taskMapper.getStuAllTask(clazzId)) {
            if (t.getExpId() != null && t.getExpId() == expId
                    && taskMapper.countCompletion(stuId, t.getId()) == 0) {
                candidates.add(t);
            }
        }
        if (candidates.isEmpty()) {
            throw new BaseException("没有待完成的该实验任务（可能已完成或任务不存在）");
        }
        candidates.sort((a, b) -> a.getEndDate().compareTo(b.getEndDate()));
        Tasks target = candidates.get(0);
        taskMapper.insertCompletion(stuId, clazzId, target.getId(), s);
        //完成人数由查询时实时统计(completions表)，不再维护tasks表快照计数
        return target;
    }

    @Override
    public Tasks getTaskDetailById(int taskId) {
        Tasks task = taskMapper.getTaskById(taskId);
        if (task == null) {
            throw new BaseException("任务不存在");
        }
        return task;
    }

    @Override
    public int countTaskCompletions(int taskId) {
        return taskMapper.countTaskCompletions(taskId);
    }
}
