package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tasks implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String teacher;
    private String clazzId;
    private Integer expId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer completeCount;
    private Integer totalCount;

}
