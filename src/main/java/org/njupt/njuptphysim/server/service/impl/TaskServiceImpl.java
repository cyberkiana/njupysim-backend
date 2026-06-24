package org.njupt.njuptphysim.server.service.impl;

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
            stuTaskInfoDTO.setTitle(resource.getTitle());
            stuTaskInfoDTO.setContent(resource.getContent());
            list.add(stuTaskInfoDTO);
        }
        return list;
    }

    @Override
    public List<StuFinishedTaskDTO> getStuFinishedTask(String stuId) {
        List<StuFinishedTaskDTO> list = new ArrayList<>();
        List<Completions> finishedTask = taskMapper.getStuAllFinishedTask(stuId);
        for (Completions completions : finishedTask) {
            StuFinishedTaskDTO sft = new StuFinishedTaskDTO();
            sft.setId(completions.getId());
            sft.setScore(completions.getScore());
            sft.setFinishDate(completions.getTime());
            Resources resource = resourcesService.getResourcesById(taskMapper.getExpIdById(completions.getTaskId()));
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
        taskMapper.deleteTask(taskId);
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
        for (Users stuOfClazz : clazzService.getStusOfClazz(clazzId)) {
            if (!completedStuIdList.contains(stuOfClazz.getId())) {
                uncompletedList.add(new StuUncompletedListDTO(stuOfClazz.getId(), stuOfClazz.getName()));
            }
        }
        return new TasksInfoVO<>(completedList, uncompletedList);
    }

    @Override
    public Tasks getExperimentDetail(String teaId, String clazzId, int expId) {
        return taskMapper.getExperimentDetail(teaId, clazzId, expId);
    }
}
