package org.njupt.njuptphysim.common.utils;

import java.util.List;

public class ListPair<T1, T2> {
    private List<T1> list1;
    private List<T2> list2;

    public ListPair(List<T1> list1, List<T2> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    public List<T1> getList1() {
        return list1;
    }

    public List<T2> getList2() {
        return list2;
    }
}
