package org.njupt.njuptphysim.pojo.dto;

import lombok.Data;

@Data
public class ResourcesUpdateDTO {

    private String oldName;// 旧名称(定位记录的唯一键)
    private String name;// 新名称
    private String createTime;// 新创建时间
    private String tag;// 新标签(可选, 不传不改)
    private String content;// 新描述(可选)
    private String url;// 新实验文件地址(可选)
    private String coverImage;// 新头图路径(可选, 一键更换头图用)

}
