package org.njupt.njuptphysim.pojo.dto;

import lombok.Data;

@Data
public class ExpListDTO {
    private Integer expId;
    private String title;
    /** 实验头图路径 */
    private String coverImage;
    /** 实验标签 */
    private String tag;
    /** 实验描述 */
    private String content;
    /** 实验资源url */
    private String url;
    /** 点赞数 */
    private Integer likes;
    /** 十分简单计数 */
    private Integer easyCount;
    /** 十分困难计数 */
    private Integer hardCount;
}
