package org.njupt.njuptphysim.pojo.vo;

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
public class ReplyVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String userName;
    private String avatar;
    private Date time;
    private String content;
    private Integer likes;
    private String repliedUserName;
}
