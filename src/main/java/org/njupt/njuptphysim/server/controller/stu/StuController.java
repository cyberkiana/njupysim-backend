package org.njupt.njuptphysim.server.controller.stu;

import org.njupt.njuptphysim.pojo.Result;
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
    @GetMapping("/reservations")
    public Result getAllStuReservations(@PathVariable String stuId){
        return Result.success(reservationService.searchReservationsOfStu(stuId));
    }

    /**
     * 学生预约
     * @param stuReservation 学生预约信息
     * @return
     */
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
    @GetMapping("/tasks")
    public Result getStuTask(@PathVariable String stuId){
        return Result.success(taskService.getStuBothExpList(stuId));
    }
}
