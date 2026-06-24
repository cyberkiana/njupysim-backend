package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StuReservations implements Serializable {

    private static final long serialVersionsUID = 1L;

    private Integer id;

    private String stuId;

    private LocalDate day;

    private Integer slot;


}
