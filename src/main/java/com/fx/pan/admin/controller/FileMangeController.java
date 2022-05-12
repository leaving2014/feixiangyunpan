package com.fx.pan.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.User;
import com.fx.pan.admin.service.FileManageService;
import com.fx.pan.service.CosFileService;
import com.fx.pan.service.FileService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.vo.FileListVo;
import com.fx.pan.vo.UserVo;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import org.checkerframework.checker.initialization.qual.FBCBottom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leaving
 * @date 2022/1/25 18:52
 * @version 1.0
 */


@RestController
@RequestMapping("/manage/file")
public class FileMangeController {

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Resource
    private FileManageService fileManageService;

    @Resource
    private CosFileService cosFileService;

    // 所有文件列表分页查看
    @GetMapping("/list")
    public ResponseResult list(@RequestParam(value = "path", required = false, defaultValue = "/") String path,
                               @RequestParam("pageNum") int pageNum,
                               @RequestParam("query") String query,
                               @RequestParam("pageSize") int pageSize,
                               @RequestParam(value = "onlyFile", defaultValue = "") boolean onlyFile,
                               @RequestParam(value = "uid", defaultValue = "") Long uid) {
        Map userMap = new HashMap();

        List<FileBean> fileBeanList = fileManageService.fileList(path, query, pageNum, pageSize, onlyFile, uid);
        for (FileBean fileBean : fileBeanList) {
            Long userId = fileBean.getUserId();
            if (userMap.get(userId) == null) {
                User user = userService.selectUserById(userId);
                userMap.put(userId, BeanCopyUtils.copyBean(user, UserVo.class));
            }
        }
        // Map userMap = fileBeanList.stream().map()
        List<FileListVo> fileListVos = fileBeanList.stream().map(fileBean -> {
            FileListVo fileListVo = BeanCopyUtils.copyBean(fileBean, FileListVo.class);
            fileListVo.setUser((UserVo) userMap.get(fileBean.getUserId()));
            return fileListVo;
        }).collect(Collectors.toList());
        Integer total = fileManageService.fileListTotal(path, query, onlyFile, pageNum, pageSize, uid);
        Map map = new HashMap();
        map.put("list", fileListVos);
        map.put("total", total);
        return ResponseResult.success(map);
        // .put("list", fileListVos).put("total", total);
    }


    // 按照文件类型查看
    @GetMapping("/type")
    public ResponseResult listType(@RequestParam("fileType") Integer fileType, @RequestParam("pageNum") int pageNum,
                                   @RequestParam("pageSize") int pageSize) {
        List<FileBean> fileBeanList = fileManageService.fileListByType(fileType, pageNum, pageSize);
        List<FileListVo> fileListVos = fileBeanList.stream().map(fileBean -> {
            FileListVo fileListVo = BeanCopyUtils.copyBean(fileBean, FileListVo.class);
            fileListVo.setUser(BeanCopyUtils.copyBean(userService.selectUserById(fileBean.getUserId()), UserVo.class));
            return fileListVo;
        }).collect(Collectors.toList());
        Integer total = fileManageService.fileListByTypeTotal(fileType);
        Map map = new HashMap();
        map.put("list", fileListVos);
        map.put("total", total);

        return ResponseResult.success(map);
        //.put("list", fileListVos).put("total", total);

    }


    @GetMapping("/search")
    public ResponseResult search(@RequestParam("keyword") String keyword, @RequestParam("pageNum") int pageNum,
                                 @RequestParam("pageSize") int pageSize) {
        List<FileBean> fileBeanList = fileManageService.search(keyword, pageNum, pageSize);
        Integer total = fileManageService.searchTotal(keyword);
        Map map = new HashMap();
        map.put("list", fileBeanList);
        map.put("total", total);
        return ResponseResult.success(map);
        //.put("list", fileBeanList).put("total", total);
    }


    @GetMapping("/download")
    public ResponseResult download(@RequestParam("file") Long[] files) {

        return ResponseResult.success("");
    }

    @PostMapping("/rename")
    public ResponseResult rename(@RequestParam("id") Long id, @RequestParam("name") String name) {
        int res = fileManageService.rename(id, name);
        return ResponseResult.success("");
    }

    @PostMapping("/delete")
    public ResponseResult delete(@RequestParam("id") Long id) {
        int i = fileManageService.deleteFileById(id);

        return ResponseResult.success("删除成功");
    }

    // 批量删除
    @PostMapping("/delete/batch")
    public ResponseResult deleteBatch(@RequestParam("ids") Long[] ids) {
        for (Long id : ids) {
            fileManageService.deleteFileById(id);
        }
        return ResponseResult.success("删除成功");
    }


    @PostMapping("/create")
    public ResponseResult create(@RequestParam("id") Long id, @RequestParam("name") String name) {

        return ResponseResult.success("");
    }

    @PostMapping("/create/folder")
    public ResponseResult createFolder(@RequestParam("id") Long id, @RequestParam("name") String name) {

        return ResponseResult.success("");
    }


    @GetMapping("/detail")
    public ResponseResult detail(@RequestParam("id") Long id) {
        FileBean fileBean = fileManageService.selectFileById(id);
        return ResponseResult.success("");
    }

    @PostMapping("/audit")
    public ResponseResult audit(@RequestParam("fileId") Long fileId, @RequestParam("audit") Integer audit) {
        int i = fileManageService.updateAudit(fileId, audit);
        if (i > 0) {
            return ResponseResult.success("审核状态成功");
        } else {
            return ResponseResult.error(500, "审核状态失败");
        }
    }

    @PostMapping("/cos/audit")
    public ResponseResult cosAudit(@RequestParam("id") Long id, @RequestParam("detectUrl") String detectUrl) {
        FileBean fileBean = fileManageService.selectFileById(id);
        Integer audit = fileBean.getAudit();
        Integer auditResult;
        ImageAuditingResponse response = cosFileService.fileAudit(fileBean, true);
        Integer terroristScore = Integer.valueOf(response.getTerroristInfo().getScore());
        Integer pornScore = Integer.valueOf(response.getPornInfo().getScore());
        Map map = new HashMap();
        map.put("terroristScore", terroristScore);
        map.put("pornScore", pornScore);
        map.put("detectUrl", response);
        if (terroristScore > 91 || pornScore > 91) {
            auditResult = -1;
        } else {
            auditResult = 1;
        }
        map.put("status", auditResult);
        if (!audit.equals(auditResult)) {
            fileManageService.updateAudit(fileBean.getId(), auditResult);
        }
        return ResponseResult.success("更新审核状态成功", map);
    }


}
