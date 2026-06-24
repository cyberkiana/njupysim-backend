package org.njupt.njuptphysim.pojo.po;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String account;

    private String password;

    private LocalDate createTime;

    private int roleId;

    private String avatar;

    private String college;
}

