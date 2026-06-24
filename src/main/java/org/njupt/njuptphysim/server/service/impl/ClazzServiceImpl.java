package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.pojo.dto.ClazzListDTO;
import org.njupt.njuptphysim.pojo.po.Clazzes;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.server.mapper.ClazzMapper;
import org.njupt.njuptphysim.server.mapper.UserClazzMapper;
import org.njupt.njuptphysim.server.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private UserClazzMapper userClazzMapper;


    @Override
    public void addClazz(Clazzes clazz) {
        //检查班级是否存在
        if (clazzMapper.selectById(clazz.getId()) != null) {
            throw new RuntimeException("班级已存在");
        }
        clazzMapper.insert(clazz);
    }

    @Override
    public void deleteClazz(String id) {
        //检查班级是否存在
        if (clazzMapper.selectById(id)== null) {
            throw new RuntimeException("班级不存在");
        }
        //如果删除的班级仍然存在用户,则不能删除
        if (!userClazzMapper.getAllUserIdByClazzId(id).isEmpty() && userClazzMapper.getAllUserIdByClazzId(id) != null ) {
            throw new RuntimeException("该班级仍有用户关联，无法删除");
        }
        //删除班级
        clazzMapper.deleteById(id);
    }

    @Override
    public void changeTeacher(String id, String teacher) {
        //检查班级是否存在
        if (clazzMapper.selectById(id)== null) {
            throw new RuntimeException("班级不存在");
        }
        //修改班级教师
        clazzMapper.updateTeacher(id, teacher);
    }

    @Override
    public List<Clazzes> getAllByPage(String id, String createTime, String teacher, int offset, int pageSize) {
        List<Clazzes> allByPage = clazzMapper.getAllByPage(id, createTime, offset, pageSize);
        for (Clazzes clazz : allByPage) {
            String teacherOfClazz = clazzMapper.getTeacherOfClazz(clazz.getId());
            clazz.setTeacher(teacherOfClazz);
        }
        return allByPage;
    }

    @Override
    public int getTotalNum(String id, String createTime, String teacher) {
        int totalNum = clazzMapper.getTotalNum(id, createTime, teacher);
        return totalNum;
    }

    @Override
    public List<Users> getStuFromClazz(String clazzId) {
        return clazzMapper.getStuFromClazz(clazzId);
    }

    @Override
    public void deleteStuFromClazz(String clazzId, String stuId) {
        clazzMapper.deleteStuFromClazz(clazzId, stuId);
    }

    @Override
    public String getClazzOfStu(String stuId) {
        return clazzMapper.getClazzOfStu(stuId);
    }

    @Override
    public int getClazzStuNum(String clazzId) {
        return clazzMapper.getClazzStuNum(clazzId);
    }

    @Override
    public List<ClazzListDTO> getTeaClazzList(String teaId) {
        List<ClazzListDTO> teaClazzList = clazzMapper.getTeaClazzList(teaId);
        System.out.println(teaClazzList);
        return teaClazzList;
    }

    @Override
    public List<Users> getStusOfClazz(String clazzId) {
        return clazzMapper.getStusOfClazz(clazzId);
    }
}
