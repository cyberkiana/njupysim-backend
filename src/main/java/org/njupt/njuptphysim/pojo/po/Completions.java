package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Completions implements Serializable {


    private Integer id;
    private String stuId;
    private String clazzId;
    private Integer taskId;
    private LocalDate time;
    private Integer score;



}
