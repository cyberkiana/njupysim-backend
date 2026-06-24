package org.njupt.njuptphysim.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StuCompletedListDTO {
    private String stuId;
    private String stuName;
    private Integer score;
}
