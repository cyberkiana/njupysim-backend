package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Reservations;
import org.njupt.njuptphysim.pojo.po.StuReservations;
import org.njupt.njuptphysim.pojo.po.Users;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    /**
     * 查询某天的预约情况
     * @param day 日期
     * @return 预约信息
     */
    List<Reservations> searchReservationsByTime(LocalDate day);

    /**
     * 学生新增预约
     * @param stuReservation 学生新预约
     */
    void addReservation(StuReservations stuReservation);

    /**
     * 获取某个学生的预约信息
     * @param stuId 学号
     * @return 预约记录
     */
    List<StuReservations> searchReservationsOfStu(String stuId);

    /**
     * 学生取消预约
     * @param id 预约id
     */
    void deleteReservation(int id, LocalDate day, int slot);

    /**
     * 管理员查看某时段预约学生名单
     * @param id
     * @return
     */
    List<UserDTO> getReserveStuList(int id);

}
