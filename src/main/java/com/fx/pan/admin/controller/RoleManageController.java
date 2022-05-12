package com.fx.pan.admin.controller;

import com.fx.pan.admin.service.RoleManageService;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/1/25 18:53
 * @version 1.0
 */

@RestController
@RequestMapping("/manage/role")
public class RoleManageController {

    @Resource
    private RoleManageService roleManageService;

    //角色列表
    @RequestMapping("/list")
    public ResponseResult list(){
      List<Role> list = roleManageService.selectRoleList();
      Map<String,Object> map = new HashMap<>();
      map.put("list",list);
      return ResponseResult.success(map);
    }
}
