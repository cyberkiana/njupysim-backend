package org.njupt.njuptphysim.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private LocalDate createTime;

    private Integer roleId;

    private String avatar;

    private String college;
}
