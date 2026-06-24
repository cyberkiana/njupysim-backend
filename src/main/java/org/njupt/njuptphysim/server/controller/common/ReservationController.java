package org.njupt.njuptphysim.server.controller.common;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.po.Reservations;
import org.njupt.njuptphysim.server.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/common/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 获取指定日期的预约信息
     * @param day 日期
     * @return
     */
    @GetMapping("/days/{day}")
    public Result searchReservationsByTime(@PathVariable LocalDate day){
        return Result.success(reservationService.searchReservationsByTime(day));
    }
}
