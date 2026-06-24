package org.njupt.njuptphysim.pojo.vo;


import lombok.Getter;
import lombok.Setter;
import org.njupt.njuptphysim.common.utils.ListPair;

import java.util.List;

// 教师查看完成和未完成实验学生名单
@Getter
@Setter
public class TasksInfoVO<T1,T2>{

    private List<T1> completedList;
    private List<T2> uncompletedList;

    public TasksInfoVO(List<T1> completedList, List<T2> uncompletedList){
        this.completedList=completedList;
        this.uncompletedList=uncompletedList;
    }
}
