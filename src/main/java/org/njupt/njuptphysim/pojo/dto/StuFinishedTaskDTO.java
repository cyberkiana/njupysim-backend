package org.njupt.njuptphysim.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

//学生已完成的实验任务
@Data
public class StuFinishedTaskDTO {
    private Integer id;             //taskId
    private String teacher;         //老师姓名
    private String title;           //实验名称
    private String content;         //实验内容
    private LocalDate finishDate;   //完成日期
    private Integer score;          //分数
}
