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
     * 预约数+1（原子操作：仅当时段允许预约且未约满时生效）
     * @param day 日期
     * @param slot 时段
     * @return 实际更新的行数（0 表示时段不可预约或已约满）
     */
    @Update("update njupt_physim.reservations set reservation_count=reservation_count+1 " +
            "where day=#{day} and slot=#{slot} and is_active=1 and reservation_count < max_count")
    int addReservationsNum(LocalDate day, int slot);

    /**
     * 预约数-1（原子操作：带下界保护，不会减到负数）
     * @param day 日期
     * @param slot 时段
     */
    @Update("update njupt_physim.reservations set reservation_count=reservation_count-1 " +
            "where day=#{day} and slot=#{slot} and reservation_count > 0")
    void  reduceReservationsNum(LocalDate day, int slot);

    /**
     * 查询某学生所有预约信息
     * @param stuId 学生id
     * @param date 日期
     * @return 预约信息
     */
    /**
     * 查询学生当前及以后的预约（不含已结束时段：day>今天, 或 day=今天且slot>=当前小时）
     * @param stuId 学生id
     * @param today 今天日期
     * @param hour 当前小时（0-23, slot即起始小时）
     * @return 预约信息
     */
    @Select("select * from njupt_physim.stu_reservations where stu_id=#{stuId} " +
            "and (day > #{today} or (day = #{today} and slot >= #{hour}))")
    List<StuReservations> searchReservationsOfStu(@Param("stuId") String stuId,
                                                  @Param("today") LocalDate today,
                                                  @Param("hour") int hour);

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
    int deleteReservationById(int id);

    /**
     * 查询学生某条预约记录
     * @param id 预约记录id
     * @return 预约记录，不存在时为 null
     */
    @Select("select * from njupt_physim.stu_reservations where id = #{id}")
    StuReservations searchStuReservationById(int id);

    /**
     * 统计学生某天某时段是否已有预约（防止同一时段重复预约）
     */
    @Select("select count(*) from njupt_physim.stu_reservations where stu_id = #{stuId} and day = #{day} and slot = #{slot}")
    int countStuReservation(@Param("stuId") String stuId, @Param("day") LocalDate day, @Param("slot") int slot);

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
     * 查询某天某时段的预约记录（用于判断该时段是否存在及 is_active 是否允许预约）
     * @param day 日期
     * @param slot 时段
     * @return 预约记录，不存在时为 null
     */
    @Select("select * from njupt_physim.reservations where day = #{day} and slot = #{slot} limit 1")
    Reservations searchReservation(LocalDate day, int slot);

    /**
     * 插入单条预约时段（定时任务/启动补漏使用，其余字段走默认值）。
     * insert ignore 配合 uk_day_slot 唯一键：重复插入静默跳过，保证幂等
     * @param day 日期
     * @param slot 时段(0-23)
     * @return 实际插入行数（0 表示该时段已存在）
     */
    @Insert("insert ignore into njupt_physim.reservations (day, slot) values (#{day}, #{slot})")
    int insertReservationSlot(LocalDate day, int slot);

    /**
     * 统计某天已有的时段行数（启动补漏使用）
     * @param day 日期
     * @return 该天在 reservations 表中的行数
     */
    @Select("select count(*) from njupt_physim.reservations where day = #{day}")
    int countSlotsByDay(LocalDate day);

    /**
     * 将某天所有预约时段置为禁止预约（定时任务使用）
     * @param day 日期
     * @return 影响行数
     */
    @Update("update njupt_physim.reservations set is_active = 0 where day = #{day}")
    int deactivateByDay(LocalDate day);

    /**
     * 管理员手动开关某时段的预约权限
     * @param id 时段id
     * @param isActive 1:允许预约 0:禁止预约
     * @return 影响行数
     */
    @Update("update njupt_physim.reservations set is_active = #{isActive} where id = #{id}")
    int updateActive(@Param("id") int id, @Param("isActive") int isActive);

    /**
     * 修改单时段最大允许人数（不得低于该时段已预约人数）
     */
    @Update("update njupt_physim.reservations set max_count = #{maxCount} where id = #{id} and reservation_count <= #{maxCount}")
    int updateMaxCount(@Param("id") int id, @Param("maxCount") int maxCount);

    /**
     * 统一设置未来时段的最大允许人数（仅更新已预约人数未超上限的时段）
     * @return 实际更新的时段数
     */
    @Update("update njupt_physim.reservations set max_count = #{maxCount} " +
            "where day >= #{today} and reservation_count <= #{maxCount}")
    int batchUpdateMaxCount(@Param("today") LocalDate today, @Param("maxCount") int maxCount);

    /**
     * 统计未来时段中已预约人数超过新上限的时段数（批量调整上限前校验）
     */
    @Select("select count(*) from njupt_physim.reservations where day >= #{today} and reservation_count > #{maxCount}")
    int countExceeding(@Param("today") LocalDate today, @Param("maxCount") int maxCount);
}
