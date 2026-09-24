package org.njupt.njuptphysim.server.service;

import org.njupt.njuptphysim.pojo.dto.ClazzListDTO;
import org.njupt.njuptphysim.pojo.po.Clazzes;
import org.njupt.njuptphysim.pojo.po.Users;

import java.time.Year;
import java.util.List;

public interface ClazzService {


    /**
     * 批量导入用：建班并挂教师（同一事务，任一步失败整体回滚）
     * @param id 班级id
     * @param createTime 年份，null 时取当前年
     * @param teacherName 教师姓名，可为空（表示暂不指定教师）
     */
    void addClazzWithTeacher(String id, Year createTime, String teacherName);

    void deleteClazz(String id);

    void changeTeacher(String id, String teacher);

    List<Clazzes> getAllByPage(String id, String createTime, String teacher, int offset, int pageSize);

    int getTotalNum(String id, String createTime, String teacher);

    java.util.List<org.njupt.njuptphysim.pojo.vo.UserVO> getStuFromClazz(String clazzId);

    void deleteStuFromClazz(String clazzId, String stuId);

    /**
     * 获取学生班级
     * @param stuId 学生id
     * @return 班级id
     */
    String getClazzOfStu(String stuId);

    /**
     * 获取班级学生人数
     * @param clazzId 班级id
     * @return 学生人数
     */
    int getClazzStuNum(String clazzId);

    /**
     * 获取教师教授班级列表
     * @param teaId 教师id
     * @return List<Clazzes>
     */
    List<ClazzListDTO> getTeaClazzList(String teaId);

    /**
     * 获取班级内所有学生信息
     * @param clazzId 班级id
     * @return List<Users>
     */
    java.util.List<org.njupt.njuptphysim.pojo.vo.UserVO> getStusOfClazz(String clazzId);
}
