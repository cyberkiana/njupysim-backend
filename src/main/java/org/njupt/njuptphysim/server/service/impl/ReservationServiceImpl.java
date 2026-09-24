package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.common.context.BaseContext;
import org.njupt.njuptphysim.common.exceptions.BaseException;
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
        //流程顺序: 防重复(同一学生同时段) -> 校验时段存在/开放 ->
        //原子扣减容量(仅 未满且开放 时生效, 防并发超卖) -> 写学生预约明细
        LocalDate day = stuReservation.getDay();
        Integer slot = stuReservation.getSlot();

        // 同一学生同一时段仅能预约一次
        if (reservationMapper.countStuReservation(stuReservation.getStuId(), day, slot) > 0) {
            throw new BaseException("此时段已预约！");
        }
        // 校验时段是否存在
        Reservations reservation = reservationMapper.searchReservation(day, slot);
        if (reservation == null) {
            throw new BaseException("该时段不存在，无法预约");
        }
        // 校验时段是否允许预约（is_active=1 允许，0 禁止）
        if (reservation.getIsActive() == null || reservation.getIsActive() != 1) {
            throw new BaseException("该时段已关闭预约，禁止此次请求");
        }
        // 原子扣减容量：仅当时段允许预约且未约满时更新生效，避免"先查再改"的并发超卖
        int updated = reservationMapper.addReservationsNum(day, slot);
        if (updated == 0) {
            throw new BaseException("该时段预约人数已满，请选择其他时段");
        }
        reservationMapper.addNewStuReservation(stuReservation.getStuId(), day, slot);
    }

    @Override
    public List<StuReservations> searchReservationsOfStu(String stuId) {
        //仅展示当前所处时段及以后的预约（已结束时段不再展示, 也无法凭其进入实验）
        LocalDate today = LocalDate.now();
        int hour = java.time.LocalTime.now().getHour();
        return reservationMapper.searchReservationsOfStu(stuId, today, hour);
    }

    @Override
    public void updateMaxCount(int id, int maxCount) {
        if (maxCount < 1 || maxCount > 40) {
            throw new BaseException("最大允许人数应在1~40之间");
        }
        //仅允许修改当天及以后的时段; 过去日期的时段已成为历史记录, 不允许再调整
        ReservationTimeDTO time = reservationMapper.searchTimeById(id);
        if (time == null || time.getDay() == null) {
            throw new BaseException("时段不存在");
        }
        if (time.getDay().isBefore(LocalDate.now())) {
            throw new BaseException("不能修改过去日期的时段最大人数");
        }
        int updated = reservationMapper.updateMaxCount(id, maxCount);
        if (updated == 0) {
            throw new BaseException("该时段已有预约人数超过新上限，请先处理");
        }
    }

    @Override
    public int batchUpdateMaxCount(int maxCount) {
        if (maxCount < 1 || maxCount > 40) {
            throw new BaseException("最大允许人数应在1~40之间");
        }
        LocalDate today = LocalDate.now();
        if (reservationMapper.countExceeding(today, maxCount) > 0) {
            throw new BaseException("部分时段已有预约人数超过新上限，请先处理");
        }
        return reservationMapper.batchUpdateMaxCount(today, maxCount);
    }

    @Override
    public void deleteReservation(int id, LocalDate day, int slot) {
        //校验预约存在且属于当前登录学生
        StuReservations sr = reservationMapper.searchStuReservationById(id);
        if (sr == null) {
            throw new BaseException("预约不存在");
        }
        if (!sr.getStuId().equals(BaseContext.getCurrentId())) {
            throw new BaseException("无权取消他人预约");
        }
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

    @Override
    public void updateActive(int id, int isActive) {
        int updated = reservationMapper.updateActive(id, isActive);
        if (updated == 0) {
            throw new BaseException("预约时段不存在");
        }
    }
}
