package org.njupt.njuptphysim.pojo.dto;

import lombok.Data;

@Data
public class ResourcesUpdateDTO {

    private String oldName;// 旧名称
    private String name;// 新名称
    private String createTime;// 新创建时间

}
