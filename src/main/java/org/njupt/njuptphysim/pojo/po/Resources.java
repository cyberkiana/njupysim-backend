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
public class Resources implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private LocalDate createTime;

    private String createrId;

    private String url;

    private String tag;

    private String content;

    private Integer likes;

    private Integer easyCount;

    private Integer hardCount;
}

