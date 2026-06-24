package org.njupt.njuptphysim.server.controller.teacher;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.pojo.po.Tasks;
import org.njupt.njuptphysim.server.service.ClazzService;
import org.njupt.njuptphysim.server.service.ResourcesService;
import org.njupt.njuptphysim.server.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tea/{teaId}")
public class TeaController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private ResourcesService resourcesService;

    /**
     * 查询教师发布的所有实验任务
     * @param teaId 教师id
     * @return 实验任务信息
     */
    @GetMapping("/tasks")
    public Result getTeaTasks(@PathVariable String teaId){
        return Result.success(taskService.getTeaTasks(teaId));
    }

    /**
     * 教师发布新实验任务
     * @param teaId 教师id
     * @param task 任务详情，
     * @return Result
     */
    @PostMapping("/tasks")
    public Result addTask(@PathVariable String teaId, @RequestBody Tasks task){
        return Result.success(taskService.addNewTask(task));
    }

    /**
     * 删除实验任务
     * @param teaId 教师id
     * @param taskId 任务id
     * @return Result
     */
    @DeleteMapping("/tasks/{taskId}")
    public Result deleteTask(@PathVariable String teaId, @PathVariable int taskId){
        return Result.success(taskService.deleteTask(teaId, taskId));
    }

    /**
     * 获取教师教授班级列表
     * @param teaId 教师id
     * @return 班级id
     */
    @GetMapping("/clazzes")
    public Result getTeaClazzList(@PathVariable String teaId){
        return Result.success(clazzService.getTeaClazzList(teaId));
    }

    /**
     * 获取班级布置的实验
     * @param clazzId 班级id
     * @return ExpListDTO
     */
    @GetMapping("/clazzes/{clazzId}")
    public Result getClazzExpList(@PathVariable String clazzId){
        return Result.success(resourcesService.getClazzExpList(clazzId));
    }

    /**
     * 教师获取完成和未完成实验学生名单
     * @param teaId 教师id
     * @param taskId 任务id
     * @param clazzId 班级id
     * @return 完成和未完成实验学生名单
     */
    @GetMapping("/tasks/{taskId}/clazzed/{clazzId}/stu")
    public Result getStuTaskInfo(@PathVariable String teaId, @PathVariable int taskId, @PathVariable String clazzId){
        return Result.success(taskService.getBothStuList(teaId, taskId, clazzId));
    }

    /**
     * 获取实验任务的具体数据
     * @param teaId 教师id
     * @param clazzId 班级id
     * @param expId 实验id
     * @return
     */
    @GetMapping("/clazzed/{clazzId}/exp/{expId}")
    public Result getExperimentDetail(@PathVariable String teaId, @PathVariable String clazzId, @PathVariable int expId){
        return Result.success(taskService.getExperimentDetail(teaId, clazzId, expId));
    }



}
