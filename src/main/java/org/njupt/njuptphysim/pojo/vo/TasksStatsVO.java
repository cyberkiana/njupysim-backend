package org.njupt.njuptphysim.pojo.vo;

import lombok.Data;

import java.time.LocalDate;

// 传递教师端已布置的实验任务信息
@Data
public class TasksStatsVO {
    private Integer id;
    private String clazzId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer completeCount;
    private Integer totalCount;
    private Integer expId;
}
