package org.njupt.njuptphysim.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StuUncompletedListDTO {
    private String stuId;
    private String stuName;
}
