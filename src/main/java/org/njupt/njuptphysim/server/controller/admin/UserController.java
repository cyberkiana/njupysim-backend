package org.njupt.njuptphysim.server.controller.admin;

import org.njupt.njuptphysim.pojo.Result;
import org.njupt.njuptphysim.pojo.po.Users;
import org.njupt.njuptphysim.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/getTotalNum")
    public Result getTotalNum(@RequestParam(required = false) String id,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) Integer roleId
                              ) {
        return Result.success(userService.getTotalNum(id, name, roleId));
    }

    @GetMapping("/getByPage")
    public Result getAllUsersByPage(@RequestParam(required = false) String id,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) Integer roleId,
                                    @RequestParam int page,
                                    @RequestParam int pageSize){
        return Result.success(userService.getByPage(id, name, roleId, (page-1)*pageSize, pageSize));
    }

    @PostMapping("/addUser")
    public Result addUser(@RequestBody Users user){
        userService.addUser(user);
        return Result.success();
    }

    @DeleteMapping("/deleteById")
    public Result deleteUser(@RequestParam String id){
        userService.deleteUser(id);
        return Result.success();
    }

    @PostMapping("/editUser")
    public Result EditUser(@RequestBody Users user){
        userService.editUser(user.getId(), user.getName(), user.getRoleId(), user.getAccount(), user.getPassword(), user.getCreateTime());
        return Result.success();
    }

}
