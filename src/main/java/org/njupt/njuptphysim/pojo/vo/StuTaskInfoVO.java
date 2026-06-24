package org.njupt.njuptphysim.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import org.njupt.njuptphysim.common.utils.ListPair;

import java.util.List;

@Getter
@Setter
// 传给学生端的已完成和未完成任务清单
public class StuTaskInfoVO<T1,T2>  {
    private List<T2> pendingTasks;
    private List<T1> completedTasks;

    public StuTaskInfoVO(List<T1> completedTasks, List<T2> pendingTasks){
        this.pendingTasks = pendingTasks;
        this.completedTasks = completedTasks;
    }
}
