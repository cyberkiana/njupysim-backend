package org.njupt.njuptphysim.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

// 学生收到的所有实验任务
@Data

public class StuTaskInfoDTO {
    private int id;                 //taskId
    private String teacher;         //老师姓名
    private String title;           //实验名称
    private String content;         //实验内容
    private LocalDate startDate;    //开始日期
    private LocalDate endDate;      //截止日期


}
