package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.po.Clazzes;
import org.njupt.njuptphysim.server.service.ClazzService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/clazzes")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;
    private UserService userService;


    /**
     * 添加班级信息
     * @param clazz 班级信息
     * @return 添加结果
     * @author
     */
    @PostMapping("")
    public Result addClazz(@RequestBody Clazzes clazz) {
        clazzService.addClazz(clazz);
        return Result.success();
    }

    /**
     * 删除班级信息
     * @param id 班级id
     * @return 删除结果
     * @author
     */
    @DeleteMapping("/{id}")
    public Result deleteClazz(@PathVariable("id") String id) {
        clazzService.deleteClazz(id);
        return Result.success();
    }

    /**
     * 根据班级ID更改时间测试接口
     * @param
     * @return 修改结果
     * @author
     */
    @PatchMapping("/{id}")
    public Result changeTime(@PathVariable("id") String id, @RequestBody String teacher) {
        String teacherId = userService.searchUserInfoByName(teacher).getId();
        clazzService.changeTeacher( id, teacherId);
        return Result.success();
    }

    /**
     * 分页查询全部班级
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("")
    public Result getAllClazzByPage(@RequestParam(required = false) String id,
                                    @RequestParam(required = false)  String createTime,
                                    @RequestParam(required = false) String teacher,
                                    @RequestParam int page,
                                    @RequestParam int pageSize){
        return Result.success(clazzService.getAllByPage(id, createTime, teacher, (page-1)*pageSize, pageSize));
    }

    /**
     * 查询本次查询的总条目数
     * @param
     * @return
     */
    @GetMapping("/totalNum")
    public Result getTotalNum( @RequestParam(required = false) String id,
                               @RequestParam(required = false)  String createTime,
                               @RequestParam(required = false) String teacher){
        return Result.success(clazzService.getTotalNum(id, createTime, teacher));
    }

    /**
     * 查询班级内学生信息
     * @param clazzId
     * @return
     */
    @GetMapping("/{clazzId}/stus")
    public Result getStuFromClazz(@PathVariable String clazzId){
        return Result.success(clazzService.getStuFromClazz(clazzId));
    }

    /**
     * 将学生移除出班级
     * @param
     * @return
     */
    @DeleteMapping("/{clazzId}/stus/{stuId}")
    public Result deleteStuFromClazz(@PathVariable String clazzId, @PathVariable String stuId){
        try {
            clazzService.deleteStuFromClazz(clazzId, stuId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("错误");
        }
    }
}
