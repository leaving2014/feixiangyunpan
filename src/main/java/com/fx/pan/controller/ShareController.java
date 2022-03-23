package com.fx.pan.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.Share;
import com.fx.pan.service.FileService;
import com.fx.pan.service.ShareService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.DateUtil;
import com.fx.pan.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件分享表(Share)表控制层
 *
 * @author leaving
 * @since 2022-01-26 23:24:21
 */
@RestController
@RequestMapping("/share")
public class ShareController {
    /**
     * 服务对象
     */

    @Autowired
    private UserService userService;
    @Resource
    private ShareService shareService;

    @Autowired
    private FileService fileService;


    /**
     * 文件分享
     */
    @PostMapping("/create")
    public Msg shareFile() {
        Long userId = SecurityUtils.getUserId();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Share share = new Share();
        Date shareTime = DateUtil.getFormatCurrentTime("yyyy-MM-dd HH:mm:ss");
        share.setShareTime(shareTime);
        share.setUserId(userId);
        // if (s)
        // TODO  文件分享
        // share.setExpired(DateUtil.plusDay());
        // share.setShareStatus(0);
        // fileService.createShareFile(fileId,shareCode,shareType,expired,userId);
        return Msg.success("分享成功");
    }


    @GetMapping("/list")
    public Msg shareList(){
        Long userId = SecurityUtils.getUserId();
        return shareService.getShareList(userId);
    }


    /**
     * 分页查询所有数据
     *
     * @param page  分页对象
     * @param share 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Msg selectAll(Page<Share> page, Share share) {
        Long userId = SecurityUtils.getUserId();

        Page<Share> page1 = shareService.page(page, new QueryWrapper<>(share));

        return Msg.success().put("list", page1);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Msg selectOne(@PathVariable Serializable id) {
        Share res = shareService.getById(id);
        return Msg.success().put("res", res);
    }

    /**
     * 新增数据
     *
     * @param share 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Msg insert(@RequestBody Share share) {
        boolean save = shareService.save(share);
        return Msg.success().put("res", save);
    }

    /**
     * 修改数据
     *
     * @param share 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Msg update(@RequestBody Share share) {
        boolean b = shareService.updateById(share);
        return Msg.success("修改成功");
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Msg delete(@RequestParam("idList") List<Long> idList) {
        boolean b = shareService.removeByIds(idList);
        return Msg.success();
    }
}

