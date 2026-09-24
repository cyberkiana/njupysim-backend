package org.njupt.njuptphysim.server.task;

import lombok.extern.slf4j.Slf4j;
import org.njupt.njuptphysim.pojo.po.SysLogRootEvent;
import org.njupt.njuptphysim.server.mapper.ReservationMapper;
import org.njupt.njuptphysim.server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 预约表启动补漏（应用启动时执行一次）：
 * 检查 今天 ~ 今天+14 共 15 天的预约时段，不足配置数量(默认18个, 6~23点)的天补齐缺失时段。
 *
 * <p>背景：每日 0 点的定时任务只在后端整点运行时生效，后端在午夜停机就会漏生成当天的
 * 目标日期（例如 09-14 凌晨停机则 09-28 不会被生成）。本钩子在每次启动时兜底补漏，
 * 依赖 reservations 表的 uk_day_slot 唯一键 + insert ignore 保证重复执行不产生重复数据。</p>
 */
@Component
@Slf4j
public class ReservationStartupInitializer implements ApplicationRunner {

    /** 补漏窗口天数：今天 ~ 今天+14 共 15 天 */
    private static final int WINDOW_DAYS = 15;

    /** 每天时段范围：6:00 ~ 24:00（slot 为起始小时），与预约页展示一致，可在 yml 调整 */
    @Value("${reservation.slot-start-hour:6}")
    private int slotStartHour;

    @Value("${reservation.slot-end-hour:23}")
    private int slotEndHour;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private LogService logService;

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime begin = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        List<String> filledDays = new ArrayList<>();
        int slotCount = slotEndHour - slotStartHour + 1;

        for (int offset = 0; offset < WINDOW_DAYS; offset++) {
            LocalDate day = today.plusDays(offset);
            if (reservationMapper.countSlotsByDay(day) >= slotCount) {
                continue;
            }
            int inserted = 0;
            for (int slot = slotStartHour; slot <= slotEndHour; slot++) {
                inserted += reservationMapper.insertReservationSlot(day, slot);
            }
            filledDays.add(day + "(补" + inserted + "个)");
        }

        if (!filledDays.isEmpty()) {
            String detail = "[定时任务] 启动补漏为 " + String.join("、", filledDays) + " 的预约时段补齐至每天" + slotCount + "个";
            log.info(detail);
            logService.saveRootEvent(SysLogRootEvent.builder()
                    .beginDate(begin)
                    .endDate(LocalDateTime.now())
                    .eventDetail(detail)
                    .createDate(LocalDateTime.now())
                    .build());
        } else {
            log.info("[定时任务] 启动补漏检查通过：今天~今天+14 的预约时段均完整");
        }
    }
}
