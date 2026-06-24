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
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roleId;

    /**
     * 修改用户 1为有权限,0为无权限
     */
    private Integer editUsers;

    /**
     * 修改资源 1为有权限,0为无权限
     */
    private Integer editResources;

    /**
     * 使用资源 1为有权限,0为无权限
     */
    private Integer useResources;
}
