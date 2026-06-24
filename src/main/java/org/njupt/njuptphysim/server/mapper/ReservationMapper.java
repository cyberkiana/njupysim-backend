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

    List<Reservations> searchReservationsByTime(LocalDate day);

    @Update("update njupt_physim.reservations set reservation_count=reservation_count+1 where day=#{day} and slot=#{slot}")
    void addReservationsNum(LocalDate day, int slot);

    @Update("update njupt_physim.reservations set reservation_count=reservation_count-1 where day=#{day} and slot=#{slot}")
    void  reduceReservationsNum(LocalDate day, int slot);

    @Select("select * from njupt_physim.stu_reservations where stu_id=#{stuId} and day>=#{date}")
    List<StuReservations> searchReservationsOfStu(String stuId, LocalDate date);

    @Insert("insert into njupt_physim.stu_reservations(stu_id, day, slot) " +
            "values (#{stuId}, #{day}, #{slot})")
    void addNewStuReservation(String stuId, LocalDate day, int slot);

    @Delete("delete from njupt_physim.stu_reservations where id=#{id}")
    void deleteReservationById(int id);

    @Select("select day, slot from njupt_physim.reservations where id=#{id}")
    ReservationTimeDTO searchTimeById(int id);

    @Select("select stu_id from njupt_physim.stu_reservations where day=#{day} and slot=#{slot}")
    List<String> searchReserveStuIdByTime(LocalDate day, int slot);
}
