package org.njupt.njuptphysim.server.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.common.utils.MiniExcelUtil;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.pojo.po.Clazzes;
import org.njupt.njuptphysim.server.service.ClazzService;
import org.njupt.njuptphysim.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin/clazzes")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;
    @Autowired
    private UserService userService;


    /**
     * 添加班级信息
     * @param clazz 班级信息
     * @return 添加结果
     * @author
     */
    @OperationLog(value = "新增", detail = "添加班级信息")
    @PostMapping("")
    public Result addClazz(@RequestBody Clazzes clazz) {
        //统一走"建班+挂教师"事务方法, 支持携带教师姓名
        clazzService.addClazzWithTeacher(clazz.getId(), clazz.getCreateTime(), clazz.getTeacher());
        return Result.success();
    }

    /**
     * Excel批量导入班级（xlsx，第一个工作表）。
     * 列顺序：班级id | 年份(可选,默认当前年) | 教师姓名(可选,须为已有教师账号的姓名)
     * 首行为表头时自动跳过；逐行独立处理，单行失败不影响其他行。
     * @param file xlsx文件
     * @return total/success/fail/errors
     */
    @OperationLog(value = "新增", detail = "Excel批量导入班级")
    @PostMapping("/import")
    public Result importClazzes(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择xlsx文件");
        }
        List<List<String>> rows;
        try {
            rows = MiniExcelUtil.read(file.getBytes());
        } catch (Exception e) {
            return Result.error("Excel解析失败，请确认上传的是 .xlsx 文件");
        }
        if (rows.isEmpty()) {
            return Result.error("Excel内容为空");
        }

        int start = looksLikeHeader(rows.get(0)) ? 1 : 0;
        List<String> errors = new ArrayList<>();
        int success = 0;

        for (int i = start; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String id = cell(row, 0), yearStr = cell(row, 1), teacherName = cell(row, 2);
            if (id.isBlank() && yearStr.isBlank() && teacherName.isBlank()) {
                continue;
            }
            int excelRow = i + 1;
            try {
                if (id.isBlank()) {
                    throw new BaseException("缺少班级id");
                }
                Year year = null;
                if (!yearStr.isBlank()) {
                    year = parseYear(yearStr);
                }
                clazzService.addClazzWithTeacher(id, year, teacherName);
                success++;
            } catch (DuplicateKeyException e) {
                errors.add("第" + excelRow + "行: 班级id已存在(" + id + ")");
            } catch (BaseException e) {
                errors.add("第" + excelRow + "行: " + e.getMessage());
            } catch (Exception e) {
                errors.add("第" + excelRow + "行: 导入失败(" + e.getMessage() + ")");
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", rows.size() - start);
        data.put("success", success);
        data.put("fail", errors.size());
        data.put("errors", errors);
        return Result.success(data);
    }

    private Year parseYear(String yearStr) {
        String digits = yearStr.replaceAll("[^0-9]", "");
        if (digits.length() != 4) {
            throw new BaseException("年份格式应为4位数字，当前为「" + yearStr + "」");
        }
        return Year.of(Integer.parseInt(digits));
    }

    /** 首行是否为表头（包含列名关键字即视为表头） */
    private boolean looksLikeHeader(List<String> row) {
        String joined = String.join("|", row);
        return joined.contains("班级") || joined.contains("教师") || joined.contains("年份");
    }

    private String cell(List<String> row, int idx) {
        return idx < row.size() && row.get(idx) != null ? row.get(idx).trim() : "";
    }

    /**
     * 删除班级信息
     * @param id 班级id
     * @return 删除结果
     * @author
     */
    @OperationLog(value = "删除", detail = "删除班级信息")
    @DeleteMapping("/{id}")
    public Result deleteClazz(@PathVariable("id") String id) {
        clazzService.deleteClazz(id);
        return Result.success();
    }

    /**
     * 更换班级指导教师
     * 请求体兼容两种形式：{"teacher":"教师姓名"} 或 原始字符串 "教师姓名"
     * @param id 班级id
     * @return 修改结果
     */
    @OperationLog(value = "修改", detail = "更换班级指导教师")
    @PatchMapping("/{id}")
    public Result changeTime(@PathVariable("id") String id, @RequestBody String body) {
        String teacher = extractTeacher(body);
        if (teacher == null || teacher.isBlank()) {
            throw new BaseException("缺少教师姓名");
        }
        //前端传教师姓名，转换为教师id
        var teacherInfo = userService.searchUserInfoByName(teacher);
        if (teacherInfo == null) {
            throw new BaseException("教师不存在");
        }
        clazzService.changeTeacher( id, teacherInfo.getId());
        return Result.success();
    }

    /** 从请求体中提取教师姓名：兼容 JSON 对象与原始字符串两种形式 */
    private String extractTeacher(String body) {
        try {
            String raw = body.trim();
            if (raw.startsWith("{")) {
                com.fasterxml.jackson.databind.JsonNode node =
                        new ObjectMapper().readTree(raw).path("teacher");
                if (!node.isMissingNode()) {
                    return node.asText();
                }
                return raw;
            }
            //去掉可能的 JSON 引号
            if (raw.length() >= 2 && raw.startsWith("\"") && raw.endsWith("\"")) {
                return raw.substring(1, raw.length() - 1);
            }
            return raw;
        } catch (Exception e) {
            throw new BaseException("请求体格式错误");
        }
    }

    /**
     * 分页查询全部班级
     * @param page
     * @param pageSize
     * @return
     */
    @OperationLog(value = "查询", detail = "分页查询班级")
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
    @OperationLog(value = "查询", detail = "查询班级总数")
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
    @OperationLog(value = "查询", detail = "查看班级学生名单")
    @GetMapping("/{clazzId}/stus")
    public Result getStuFromClazz(@PathVariable String clazzId){
        return Result.success(clazzService.getStuFromClazz(clazzId));
    }

    /**
     * 将学生移除出班级
     * @param
     * @return
     */
    @OperationLog(value = "删除", detail = "将学生移出班级")
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
