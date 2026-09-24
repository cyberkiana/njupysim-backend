package org.njupt.njuptphysim.server.controller.stu;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.pojo.po.StuReservations;
import org.njupt.njuptphysim.server.service.ReservationService;
import org.njupt.njuptphysim.server.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/stu/{stuId}")
public class StuController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TaskService taskService;


    /**
     * 获取某学生的预约记录
     * @param stuId 学生id
     * @return
     */
    @OperationLog(value = "查询", detail = "查看学生预约记录")
    @GetMapping("/reservations")
    public Result getAllStuReservations(@PathVariable String stuId){
        return Result.success(reservationService.searchReservationsOfStu(stuId));
    }

    /**
     * 学生预约
     * @param stuReservation 学生预约信息
     * @return
     */
    @OperationLog(value = "预约", detail = "学生预约实验时段")
    @PostMapping("/reservations")
    public Result addReservation(@RequestBody StuReservations stuReservation){
        reservationService.addReservation(stuReservation);
        return Result.success();
    }

    /**
     * 学生取消预约
     * @param id 预约id
     * @return
     */
    @OperationLog(value = "取消", detail = "学生取消预约")
    @DeleteMapping("/reservations/{id}")
    public Result cancelReservation(@PathVariable int id, @RequestParam LocalDate day, @RequestParam int slot){
        reservationService.deleteReservation(id, day, slot);
        return Result.success();
    }

    /**
     * 查看学生已完成和未完成任务清单
     * @param stuId 学生id
     * @return 已完成和未完成任务清单
     */
    @OperationLog(value = "查询", detail = "查看学生任务清单")
    @GetMapping("/tasks")
    public Result getStuTask(@PathVariable String stuId){
        return Result.success(taskService.getStuBothExpList(stuId));
    }

    /**
     * 学生完成WebGL实验后提交成绩：写入 completions 表并累加任务完成人数。
     * 同一学生同一任务仅记录一次；若有多个相同实验的任务，按截止日期最早优先记录。
     * @param stuId 学生id
     * @param body { expId: 实验id, score: 成绩(可选,默认100) }
     */
    @OperationLog(value = "新增", detail = "提交WebGL实验成绩")
    @PostMapping("/tasks/complete")
    public Result completeTask(@PathVariable String stuId, @RequestBody java.util.Map<String, Object> body){
        if (!stuId.equals(org.njupt.njuptphysim.common.context.BaseContext.getCurrentId())) {
            throw new org.njupt.njuptphysim.common.exceptions.BaseException("无权提交其他学生的实验成绩");
        }
        if (body.get("expId") == null) {
            throw new org.njupt.njuptphysim.common.exceptions.BaseException("缺少实验id");
        }
        int expId = Integer.parseInt(body.get("expId").toString());
        Integer score = body.get("score") == null ? null : Integer.valueOf(body.get("score").toString());
        org.njupt.njuptphysim.pojo.po.Tasks task = taskService.completeTask(stuId, expId, score);
        java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("taskId", task.getId());
        data.put("expId", task.getExpId());
        data.put("endDate", task.getEndDate());
        data.put("score", score == null ? 100 : score);
        return Result.success(data);
    }
}
