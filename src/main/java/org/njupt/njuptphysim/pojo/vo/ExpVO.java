package org.njupt.njuptphysim.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    /** 实验头图路径 */
    private String coverImage;

    private String url;

    private Integer likes;

    private Integer easyCount;

    private Integer hardCount;
}
