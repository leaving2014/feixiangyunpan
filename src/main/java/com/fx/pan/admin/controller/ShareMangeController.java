package com.fx.pan.admin.controller;

import com.fx.pan.admin.service.ShareManageService;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.common.Constants;
import com.fx.pan.domain.User;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.BeanCopyUtils;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.vo.UserVo;
import com.fx.pan.vo.share.ShareFileListVO;
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
@RequestMapping("/manage/share")
public class ShareMangeController {

    @Resource
    private ShareManageService shareManageService;

    @Resource
    private UserService userService;

    @Resource
    private RedisCache redisCache;

    @GetMapping("list")
    public ResponseResult shareList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value =
            "pageSize", defaultValue = "20") Integer pageSize){
        Map userMap = new HashMap();

        // List<Share> list = shareManageService.shareList(pageNum, pageSize);
        List<ShareFileListVO> shareFileListVOS = shareManageService.shareList(pageNum, pageSize);
        // 遍历shareFileListVOS，获取userId查询用户信息，并将用户信息放入userMap中
        for (ShareFileListVO shareFileListVO : shareFileListVOS) {
            Long userId = shareFileListVO.getUserId();
            if (userMap.get(userId) == null){
                User user = userService.selectUserById(userId);
                userMap.put(userId,BeanCopyUtils.copyBean(user,UserVo.class));
            }
        }
                // BeanCopyUtils.copyBeanList(list, ShareFileListVO.class);

        // List<ShareFileListVO> list = shareService.selectShareFileList(userId);
        List<ShareFileListVO> listWithoutDuplicates = shareFileListVOS.stream().map(e->{
            e.setUser((UserVo) userMap.get(e.getUserId()));
            return e;
        }).distinct().collect(Collectors.toList());

        listWithoutDuplicates.forEach(item -> {
            Double zsetScore = redisCache.getZsetScore(Constants.REDIS_DATA_SUFFIX + "-share-bt", item.getId());
            item.setBrowseTimes(zsetScore == null ? 0L : zsetScore.intValue());

        });

        Integer total = shareManageService.shareListTotal();
        Map map = new HashMap();
        map.put("list",listWithoutDuplicates);
        map.put("total",total);
        return ResponseResult.success(map);
    }


    @PostMapping("/delete")
    public ResponseResult deleteShare(@RequestParam("id") Long id){
        int i = shareManageService.deleteShare(id);
        if (i > 0){
            return ResponseResult.success("删除成功");
        } else {
            return ResponseResult.error(500,"删除失败");
        }
    }

    @PostMapping("/clear")
    public ResponseResult clearShare(){
        int i = shareManageService.clearShare();
        if (i > 0){
            return ResponseResult.success("清空成功");
        } else {
            return ResponseResult.error(500,"清空失败或没有过期的文件");
        }
    }
}
