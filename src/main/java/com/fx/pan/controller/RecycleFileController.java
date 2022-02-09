package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.service.FileService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.SessionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author leaving
 * @Date 2022/2/6 13:18
 * @Version 1.0
 */
@Tag(name = "recycle", description = "该接口为回收站文件操作接口")
@Slf4j
@RequestMapping(value = "/recycle")
@RestController
public class RecycleFileController {

    @Autowired
    private UserService userService;

    @Resource
    private FileService fileService;

    /**
     * 获取回收站文件
     * @return
     */
    @GetMapping("/list")
    public Msg fileRecycle(@RequestHeader("Authorization") String token){
        Long id = userService.getUserBeanByToken(token).getId();
        List<FileBean> list = fileService.getRecycleList(id);
        return Msg.success("获取成功").put("list", list).put("count", list.size());
    }

    /**
     *还原回收站文件
     * @param filelist
     */
    @PostMapping("/restore")
    public Msg restoreFile(@RequestBody List<Long> filelist){
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();
        for (Long fid : filelist) {
            fileService.restoreFile(fid,userId);
        }
        return Msg.success("还原成功").put("count", filelist.size());
    }

    @PostMapping("/delete")
    public Msg deleteRecycleFile(@RequestParam("id") Long id){
        LoginUser sessionUserBean = (LoginUser) SessionUtil.getSession();
        Long userId = sessionUserBean.getUserId();

        boolean flag = fileService.deleteRecycleFileById(id,userId);
        if (flag) {
            return Msg.success("删除成功");
        }else{
            return Msg.error(500, "删除失败");
        }
    }


}
