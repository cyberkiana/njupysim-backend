package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comments implements Serializable {


    private Integer id;
    private String userId;
    private Integer resourceId;
    private Date time;
    private String content;
    private Integer likes;
    private Integer visible;



}
