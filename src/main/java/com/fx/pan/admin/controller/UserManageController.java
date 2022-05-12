package com.fx.pan.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.fx.pan.admin.service.UserManageService;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/1/25 18:52
 * @version 1.0
 */

@RestController
@RequestMapping("/manage/user")
public class UserManageController {

    @Resource
    private UserManageService userManageService;

    @RequestMapping("/list")
    public ResponseResult list(@RequestParam(value = "query",required = false) String query,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        List<User> users = userManageService.selectUsers(query,pageNum,pageSize);
        Integer total = userManageService.selectUsersTotal(query);
        Map<String, Object> map = new HashMap<>();
        map.put("users", users);
        map.put("total", total);
        map.put("list", users);
        return ResponseResult.success(map);
    }

    @PostMapping("/changestatus")
    public ResponseResult changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        int i = userManageService.changeStatus(id,status);
        if (i == 1) {
            return ResponseResult.success("用户状态更新成功");
        } else {
            return ResponseResult.error(500,"用户状态更新失败");
        }
    }

    @GetMapping("/detail")
    public ResponseResult detail(@RequestParam("id") Long id) {
        User user = userManageService.selectUserById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        return ResponseResult.success(user);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody User user) {
        int i = userManageService.updateUser(user);
        if (i == 1) {
            return ResponseResult.success("用户更新成功");
        } else {
            return ResponseResult.error(500,"用户更新失败");
        }
    }



}
