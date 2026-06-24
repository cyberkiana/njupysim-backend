package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.pojo.dto.ReservationTimeDTO;
import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Reservations;
import org.njupt.njuptphysim.pojo.po.StuReservations;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.server.mapper.ReservationMapper;
import org.njupt.njuptphysim.server.service.ReservationService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private UserService userService;

    @Override
    public List<Reservations> searchReservationsByTime(LocalDate day) {
        return reservationMapper.searchReservationsByTime(day);
    }

    @Override
    public void addReservation(StuReservations stuReservation) {
        System.out.println(stuReservation);
        reservationMapper.addNewStuReservation(stuReservation.getStuId(),stuReservation.getDay(),stuReservation.getSlot());
        reservationMapper.addReservationsNum(stuReservation.getDay(), stuReservation.getSlot());
    }

    @Override
    public List<StuReservations> searchReservationsOfStu(String stuId) {
        LocalDate date = LocalDate.now();
        return reservationMapper.searchReservationsOfStu(stuId, date);
    }

    @Override
    public void deleteReservation(int id, LocalDate day, int slot) {
        reservationMapper.deleteReservationById(id);
        reservationMapper.reduceReservationsNum(day, slot);
    }

    @Override
    public List<UserDTO> getReserveStuList(int id) {
        List<UserDTO> list = new ArrayList<>();
        ReservationTimeDTO reservationTimeDTO = reservationMapper.searchTimeById(id);
        List<String> ids = reservationMapper.searchReserveStuIdByTime(reservationTimeDTO.getDay(), reservationTimeDTO.getSlot());
        for (String s : ids) {
            UserDTO userDTO = userService.searchBaseInfoById(s);
            list.add(userDTO);
        }
        return list;
    }
}
