package org.njupt.njuptphysim.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Replies implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String userId;
    private Integer commentId;
    private Date time;
    private String content;
    private Integer likes;
    private Integer visible;
    private String repliedUserName;


}
