package org.njupt.njuptphysim.pojo.po;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserClass implements Serializable {

    private static final long serialVersionUID = 1L;

    private String classId;

    private String userId;

    private Integer userRoleId;
}

