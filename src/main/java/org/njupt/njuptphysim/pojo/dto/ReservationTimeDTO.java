package org.njupt.njuptphysim.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationTimeDTO {
    private int slot;
    private LocalDate day;
}
