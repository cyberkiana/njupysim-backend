package org.njupt.njuptphysim.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String account;

    private LocalDate createTime;

    private int roleId;

    private String avatar;

    private String college;
}
