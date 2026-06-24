package org.njupt.njuptphysim.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clazzes implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Year createTime;

    private String teacher;
}

