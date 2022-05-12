package com.fx.pan.admin.controller;

import com.fx.pan.admin.service.RecycleManageService;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.FileBean;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/manage/recycle")
public class RecycleManageController {

    @Resource
    private RecycleManageService recycleManageService;

    @GetMapping("/list")
    public ResponseResult list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "query",required = false) String query) {
        List<FileBean> recycleList = recycleManageService.selectRecycleList(pageNum, pageSize,query);
        Integer total = recycleManageService.selectRecycleCount(query);
        Map map = new HashMap();
        map.put("total", total);
        map.put("list", recycleList);
        return ResponseResult.success(map);
    }

    @PostMapping("/recover")
    public ResponseResult recover(@RequestBody List<Long> files) {
        for (Long id : files) {
            recycleManageService.recoverFile(id);
        }
        return ResponseResult.success("恢复成功");

    }

    @PostMapping("/delete")
    public ResponseResult delete(@RequestBody List<Long> files) {
        for (Long id : files) {
            int i = recycleManageService.deleteRecycleFile(id);
        }
        return ResponseResult.success("删除成功");
    }
}
