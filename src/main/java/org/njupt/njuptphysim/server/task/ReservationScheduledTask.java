package org.njupt.njuptphysim.server.task;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.pojo.po.SysLogRootEvent;
import org.njupt.njuptphysim.server.mapper.ReservationMapper;
import org.njupt.njuptphysim.server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约表定时任务（每天晚上 0 点执行）：
 * <ol>
 *   <li>为 14 天后的日期生成 24 个时段（slot=0..23）的预约记录；</li>
 *   <li>将前一天（day=昨天）的所有预约时段置为不可预约（is_active=0）。</li>
 * </ol>
 * 每次执行结果写入 sys_log_root_event 管理员事件表并输出控制台日志。
 */
@Component
@Slf4j
public class ReservationScheduledTask {

    /** 每天提前生成的天数偏移：当天日期+14天 */
    private static final int ADVANCE_DAYS = 14;

    /** 每天时段范围：6:00 ~ 24:00（slot 为起始小时，与预约页展示一致），可在 yml 调整 */
    @Value("${reservation.slot-start-hour:6}")
    private int slotStartHour;

    @Value("${reservation.slot-end-hour:23}")
    private int slotEndHour;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private LogService logService;

    /**
     * 每天晚上 12 点（0点0分）向 reservations 表插入时段数据：
     * day 为当天日期+14天，slot 为 6-23（对应 6:00~24:00），其余字段走默认值
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void generateDailyReservations() {
        LocalDateTime begin = LocalDateTime.now();
        LocalDate targetDay = LocalDate.now().plusDays(ADVANCE_DAYS);

        int inserted = 0;
        for (int slot = slotStartHour; slot <= slotEndHour; slot++) {
            inserted += reservationMapper.insertReservationSlot(targetDay, slot);
        }

        logService.saveRootEvent(SysLogRootEvent.builder()
                .beginDate(begin)
                .endDate(LocalDateTime.now())
                .eventDetail(String.format("[定时任务] 系统定时任务为 %s 生成 %d 个预约时段（slot 0-23）",
                        targetDay, inserted))
                .createDate(LocalDateTime.now())
                .build());
    }

    /**
     * 每天晚上 12 点（0点0分）将 reservations 表中 day 等于前一天的所有行 is_active 置为 0
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deactivateExpiredReservations() {
        LocalDateTime begin = LocalDateTime.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        int updated = reservationMapper.deactivateByDay(yesterday);

        logService.saveRootEvent(SysLogRootEvent.builder()
                .beginDate(begin)
                .endDate(LocalDateTime.now())
                .eventDetail(String.format("[定时任务] 系统定时任务将 %s 的 %d 个预约时段设为禁止预约（is_active=0）",
                        yesterday, updated))
                .createDate(LocalDateTime.now())
                .build());
    }
}
