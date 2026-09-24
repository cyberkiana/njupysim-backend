package org.njupt.njuptphysim.server.service.impl;

import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.pojo.dto.ClazzListDTO;
import org.njupt.njuptphysim.pojo.dto.UserDTO;
import org.njupt.njuptphysim.pojo.po.Clazzes;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.server.mapper.ClazzMapper;
import org.njupt.njuptphysim.server.mapper.UserClazzMapper;
import org.njupt.njuptphysim.server.service.ClazzService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Transactional
@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private UserClazzMapper userClazzMapper;
    @Autowired
    private UserService userService;

    @Override
    public void addClazzWithTeacher(String id, Year createTime, String teacherName) {
        if (clazzMapper.selectById(id) != null) {
            throw new BaseException("班级已存在");
        }
        Clazzes clazz = Clazzes.builder()
                .id(id)
                .createTime(createTime == null ? Year.now() : createTime)
                .build();
        clazzMapper.insert(clazz);
        if (teacherName != null && !teacherName.isBlank()) {
            UserDTO teacher = userService.searchUserInfoByName(teacherName);
            if (teacher == null) {
                throw new BaseException("教师「" + teacherName + "」不存在");
            }
            if (teacher.getRoleId() == null || teacher.getRoleId() != 2) {
                throw new BaseException("「" + teacherName + "」不是教师，无法挂到班级");
            }
            clazzMapper.insertClassTeacher(id, teacher.getId());
        }
    }

    @Override
    public void deleteClazz(String id) {
        //检查班级是否存在
        if (clazzMapper.selectById(id)== null) {
            throw new BaseException("班级不存在");
        }
        //教师关联(role_id=2)随班级一并删除，仅当班级内仍有学生时禁止删除
        if (clazzMapper.getClazzStuNum(id) > 0) {
            throw new BaseException("该班级仍有学生，无法删除");
        }
        userClazzMapper.deleteByClazzId(id);
        //删除班级
        clazzMapper.deleteById(id);
    }

    @Override
    public void changeTeacher(String id, String teacher) {
        //检查班级是否存在
        if (clazzMapper.selectById(id)== null) {
            throw new BaseException("班级不存在");
        }
        //修改班级教师；该班尚无教师关联行时改为插入
        int updated = clazzMapper.updateTeacher(id, teacher);
        if (updated == 0) {
            clazzMapper.insertClassTeacher(id, teacher);
        }
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
    public java.util.List<org.njupt.njuptphysim.pojo.vo.UserVO> getStuFromClazz(String clazzId) {
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
    public java.util.List<org.njupt.njuptphysim.pojo.vo.UserVO> getStusOfClazz(String clazzId) {
        return clazzMapper.getStusOfClazz(clazzId);
    }
}
