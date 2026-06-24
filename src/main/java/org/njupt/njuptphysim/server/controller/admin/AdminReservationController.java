package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.server.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/{id}/stus")
    public List<UserDTO> getReserveStuList(@PathVariable int id){
        System.out.println("111");
        return reservationService.getReserveStuList(id);
    }
}
