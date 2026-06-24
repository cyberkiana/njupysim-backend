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

public class Reservations implements Serializable {


    private static final long serialVersionsUID = 1L;
    private Integer id;
    private LocalDate day;
    private Integer slot;
    private Integer reservationCount;
    private Integer maxCount;


}
