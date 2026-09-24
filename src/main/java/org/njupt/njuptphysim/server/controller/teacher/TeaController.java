package org.njupt.njuptphysim.server.controller.teacher;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.po.Resources;
import org.njupt.njuptphysim.pojo.po.Tasks;
import org.njupt.njuptphysim.server.service.ClazzService;
import org.njupt.njuptphysim.server.service.ResourcesService;
import org.njupt.njuptphysim.server.service.TaskService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/tea/{teaId}")
public class TeaController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private UserService userService;

    /**
     * 查询教师发布的所有实验任务
     * @param teaId 教师id
     * @return 实验任务信息
     */
    @OperationLog(value = "查询", detail = "查看教师任务列表")
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
    @OperationLog(value = "新增", detail = "教师发布实验任务")
    @PostMapping("/tasks")
    public Result addTask(@PathVariable String teaId, @RequestBody Tasks task){
        checkSelf(teaId);
        //教师姓名以登录身份为准，不信任请求体，保证与任务列表按姓名查询一致
        task.setTeacher(userService.getNameById(teaId));
        return Result.success(taskService.addNewTask(task));
    }

    /**
     * 删除实验任务
     * @param teaId 教师id
     * @param taskId 任务id
     * @return Result
     */
    @OperationLog(value = "删除", detail = "教师删除实验任务")
    @DeleteMapping("/tasks/{taskId}")
    public Result deleteTask(@PathVariable String teaId, @PathVariable int taskId){
        checkSelf(teaId);
        return Result.success(taskService.deleteTask(teaId, taskId));
    }

    /** 校验路径中的教师id与登录token身份一致，防止冒用他人身份读写 */
    private void checkSelf(String teaId) {
        if (!teaId.equals(BaseContext.getCurrentId())) {
            throw new BaseException("无权操作其他教师的数据");
        }
    }

    /**
     * 获取教师教授班级列表
     * @param teaId 教师id
     * @return 班级id
     */
    @OperationLog(value = "查询", detail = "查看教师班级列表")
    @GetMapping("/clazzes")
    public Result getTeaClazzList(@PathVariable String teaId){
        return Result.success(clazzService.getTeaClazzList(teaId));
    }

    /**
     * 获取班级布置的实验
     * @param clazzId 班级id
     * @return ExpListDTO
     */
    @OperationLog(value = "查询", detail = "查看班级实验列表")
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
    @OperationLog(value = "查询", detail = "查看学生任务完成情况")
    @GetMapping("/tasks/{taskId}/clazzed/{clazzId}/stu")
    public Result getStuTaskInfo(@PathVariable String teaId, @PathVariable int taskId, @PathVariable String clazzId){
        return Result.success(taskService.getBothStuList(teaId, taskId, clazzId));
    }


    /**
     * 按任务id查询任务详情（同一实验被多个任务布置时，各任务数据按任务id相互区分）
     * @param teaId 教师id
     * @param taskId 任务id
     * @return 任务详情(含实验名称)
     */
    @OperationLog(value = "查询", detail = "按任务id查看实验任务数据")
    @GetMapping("/tasks/{taskId}/detail")
    public Result getTaskDetail(@PathVariable String teaId, @PathVariable int taskId){
        checkSelf(teaId);
        Tasks task = taskService.getTaskDetailById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        //补充实验名称便于前端展示
        Resources resource = resourcesService.getResourcesById(task.getExpId());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", task.getId());
        data.put("teacher", task.getTeacher());
        data.put("clazzId", task.getClazzId());
        data.put("expId", task.getExpId());
        data.put("startDate", task.getStartDate());
        data.put("endDate", task.getEndDate());
        //完成数/总人数实时统计(completions表+当前班级名单), 与下方学生名单表格同口径
        data.put("completeCount", taskService.countTaskCompletions(taskId));
        data.put("totalCount", clazzService.getClazzStuNum(task.getClazzId()));
        data.put("title", resource != null ? resource.getTitle() : "未知实验");
        return Result.success(data);
    }

}
