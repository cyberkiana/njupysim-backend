package org.njupt.njuptphysim.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.njupt.njuptphysim.pojo.dto.ReservationTimeDTO;
import org.njupt.njuptphysim.pojo.po.Reservations;
import org.njupt.njuptphysim.pojo.po.StuReservations;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReservationMapper{

    /**
     * 查询某天所有时段的具体预约情况
     * @param day 日期
     * @return 预约
     */
    List<Reservations> searchReservationsByTime(LocalDate day);

    /**
     * 预约数+1
     * @param day 日期
     * @param slot 时段
     */
    @Update("update njupt_physim.reservations set reservation_count=reservation_count+1 where day=#{day} and slot=#{slot}")
    void addReservationsNum(LocalDate day, int slot);

    /**
     * 预约数-1
     * @param day 日期
     * @param slot 时段
     */
    @Update("update njupt_physim.reservations set reservation_count=reservation_count-1 where day=#{day} and slot=#{slot}")
    void  reduceReservationsNum(LocalDate day, int slot);

    /**
     * 查询某学生所有预约信息
     * @param stuId 学生id
     * @param date 日期
     * @return 预约信息
     */
    @Select("select * from njupt_physim.stu_reservations where stu_id=#{stuId} and day<=#{date}")
    List<StuReservations> searchReservationsOfStu(String stuId, LocalDate date);

    /**
     * 添加预约信息
     * @param stuId 学生id
     * @param day 日期
     * @param slot 时段
     */
    @Insert("insert into njupt_physim.stu_reservations(stu_id, day, slot) " +
            "values (#{stuId}, #{day}, #{slot})")
    void addNewStuReservation(String stuId, LocalDate day, int slot);

    /**
     * 根据id删除学生预约信息
     * @param id 学生预约id
     */
    @Delete("delete from njupt_physim.stu_reservations where id=#{id}")
    void deleteReservationById(int id);

    /**
     * 根据id查询预约时段
     * @param id 时段id
     * @return day，slot
     */
    @Select("select day, slot from njupt_physim.reservations where id=#{id}")
    ReservationTimeDTO searchTimeById(int id);

    /**
     * 查询某时段内所有预约学生id
     * @param day 日期
     * @param slot 时段
     * @return 学生id
     */
    @Select("select stu_id from njupt_physim.stu_reservations where day=#{day} and slot=#{slot}")
    List<String> searchReserveStuIdByTime(LocalDate day, int slot);

    /**
     * 查询时段已预约人数
     * @param day 日期
     * @param slot 时间段
     * @return 已预约数
     */
    @Select("select reservation_count from njupt_physim.reservations where day = #{day} and slot = #{slot}")
    int searchReservationCount(LocalDate day, int slot);

    /**
     * 查询该时段最大可预约人数
     * @param day 日期
     * @param slot 时间段
     * @return 最大可预约数
     */
    @Select("select max_count from njupt_physim.reservations where day = #{day} and slot = #{slot}")
    int searchMaxReservationCount(LocalDate day, int slot);
}
