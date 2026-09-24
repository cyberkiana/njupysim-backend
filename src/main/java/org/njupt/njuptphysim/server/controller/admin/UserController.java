package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.common.exceptions.BaseException;
import org.njupt.njuptphysim.common.utils.MiniExcelUtil;
import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.common.annotation.OperationLog;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @OperationLog(value = "查询", detail = "查询用户总数")
    @GetMapping("/getTotalNum")
    public Result getTotalNum(@RequestParam(required = false) String id,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) Integer roleId
                              ) {
        return Result.success(userService.getTotalNum(id, name, roleId));
    }

    @OperationLog(value = "查询", detail = "分页查询用户")
    @GetMapping("/getByPage")
    public Result getAllUsersByPage(@RequestParam(required = false) String id,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) Integer roleId,
                                    @RequestParam int page,
                                    @RequestParam int pageSize){
        return Result.success(userService.getByPage(id, name, roleId, (page-1)*pageSize, pageSize));
    }

    @OperationLog(value = "新增", detail = "添加用户")
    @PostMapping("/addUser")
    public Result addUser(@RequestBody Users user){
        userService.addUser(user);
        return Result.success();
    }

    @OperationLog(value = "删除", detail = "删除用户")
    @DeleteMapping("/deleteById")
    public Result deleteUser(@RequestParam String id){
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 重置用户密码为初始密码123456
     * @param id 用户id
     */
    @OperationLog(value = "修改", detail = "重置用户密码")
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestParam String id){
        userService.resetPassword(id);
        return Result.success();
    }

    /**
     * 删除用户头像
     * @param id 用户id
     */
    @OperationLog(value = "修改", detail = "删除用户头像")
    @PostMapping("/clearAvatar")
    public Result clearAvatar(@RequestParam String id){
        userService.clearAvatar(id);
        return Result.success();
    }

    @OperationLog(value = "修改", detail = "编辑用户信息")
    @PostMapping("/editUser")
    public Result EditUser(@RequestBody Users user){
        userService.editUser(user.getId(), user.getName(), user.getRoleId(), user.getAccount(), user.getPassword(), user.getCreateTime());
        return Result.success();
    }

    /**
     * Excel批量导入用户（xlsx，第一个工作表）。
     * 列顺序：用户id | 姓名 | 密码 | 角色(学生/教师/管理员或1/2/3) | 账号(可选,默认同id) | 学院(可选)
     * 首行为表头时自动跳过；逐行独立处理，单行失败不影响其他行。
     * @param file xlsx文件
     * @return total/success/fail/errors
     */
    @OperationLog(value = "新增", detail = "Excel批量导入用户")
    @PostMapping("/import")
    public Result importUsers(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择xlsx文件");
        }
        List<List<String>> rows;
        try {
            rows = MiniExcelUtil.read(file.getBytes());
        } catch (Exception e) {
            logger.warn("用户导入Excel解析失败: {}", e.getMessage());
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
            String id = cell(row, 0), name = cell(row, 1), password = cell(row, 2);
            String roleStr = cell(row, 3), account = cell(row, 4), college = cell(row, 5);
            if (id.isBlank() && name.isBlank() && password.isBlank()) {
                continue;
            }
            int excelRow = i + 1;
            try {
                if (id.isBlank()) {
                    throw new BaseException("缺少用户id");
                }
                if (name.isBlank()) {
                    throw new BaseException("缺少姓名");
                }
                if (password.isBlank()) {
                    throw new BaseException("缺少密码");
                }
                int roleId = parseRole(roleStr);
                Users user = Users.builder()
                        .id(id).name(name).password(password).roleId(roleId)
                        .account(account.isBlank() ? id : account)
                        .college(college)
                        .build();
                userService.addUser(user);
                success++;
            } catch (DuplicateKeyException e) {
                errors.add("第" + excelRow + "行: 用户id已存在(" + id + ")");
            } catch (BaseException e) {
                errors.add("第" + excelRow + "行: " + e.getMessage());
            } catch (Exception e) {
                logger.warn("用户导入第{}行失败: {}", excelRow, e.getMessage());
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

    /** 首行是否为表头（包含列名关键字即视为表头） */
    private boolean looksLikeHeader(List<String> row) {
        String joined = String.join("|", row);
        for (String keyword : new String[]{"姓名", "密码", "角色", "账号", "学院", "工号", "学号"}) {
            if (joined.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /** 角色：学生/教师/管理员 或 3/2/1（遵循 roles 表：1管理员 2教师 3学生） */
    private int parseRole(String roleStr) {
        String s = roleStr == null ? "" : roleStr.trim();
        return switch (s) {
            case "学生", "student", "3" -> 3;
            case "教师", "老师", "teacher", "2" -> 2;
            case "管理员", "admin", "1" -> 1;
            default -> throw new BaseException("角色应为 学生/教师/管理员 或 3/2/1，当前为「" + s + "」");
        };
    }

    private String cell(List<String> row, int idx) {
        return idx < row.size() && row.get(idx) != null ? row.get(idx).trim() : "";
    }

}
