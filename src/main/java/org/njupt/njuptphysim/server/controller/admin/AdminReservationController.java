package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.server.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    @Autowired
    private ReservationService reservationService;

    @OperationLog(value = "查询", detail = "查看时段预约学生名单")
    @GetMapping("/{id}/stus")
    public List<UserDTO> getReserveStuList(@PathVariable int id){
        return reservationService.getReserveStuList(id);
    }

    /**
     * 开关某时段的预约权限（is_active: 1允许 0禁止）
     */
    @OperationLog(value = "修改", detail = "设置时段预约开关")
    @PatchMapping("/{id}/active")
    public Result updateActive(@PathVariable int id, @RequestParam int isActive) {
        reservationService.updateActive(id, isActive);
        return Result.success();
    }

    /**
     * 修改单时段最大允许人数
     * @param id 时段id
     * @param maxCount 最大人数(1~40, 不得低于已预约人数)
     */
    @OperationLog(value = "修改", detail = "修改时段最大允许人数")
    @PostMapping("/{id}/max-count")
    public Result updateMaxCount(@PathVariable int id, @RequestParam int maxCount) {
        reservationService.updateMaxCount(id, maxCount);
        return Result.success();
    }

    /**
     * 统一设置未来时段的最大允许人数
     * @param maxCount 最大人数(1~40)
     */
    @OperationLog(value = "修改", detail = "统一设置时段最大允许人数")
    @PostMapping("/batch-max-count")
    public Result batchUpdateMaxCount(@RequestParam int maxCount) {
        return Result.success(reservationService.batchUpdateMaxCount(maxCount));
    }
}
