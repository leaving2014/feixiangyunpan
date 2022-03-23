package com.fx.pan.component;

/**
 * @Author leaving
 * @Date 2022/3/10 14:36
 * @Version 1.0
 */

import com.alibaba.fastjson.JSON;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Share;
import com.fx.pan.domain.User;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.service.FileService;
import com.fx.pan.service.ShareService;
import com.fx.pan.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件逻辑处理组件
 */
@Slf4j
@Component
public class FileDealComp {

    @Resource
    FileMapper fileMapper;
    @Resource
    UserService userService;
    @Resource
    ShareService shareService;
    // @Resource
    // IShareFileService shareFileService;
    @Resource
    FileService fileService;


    /**
     * 根据用户传入的参数，判断是否有下载或者预览权限
     * @return
     */
    public boolean checkAuthDownloadAndPreview(String shareBatchNum,
                                               String extractionCode,
                                               String token,
                                               long fileId) {
        log.debug("权限检查开始：shareBatchNum:{}, extractionCode:{}, token:{}, userFileId{}" , shareBatchNum,
                extractionCode, token, fileId);
        FileBean userFile = fileService.getById(fileId);
        log.debug(JSON.toJSONString(userFile));
        if ("undefined".equals(shareBatchNum)  || StringUtils.isEmpty(shareBatchNum)) {

            User sessionUserBean = userService.getUserBeanByToken(token);
            log.debug(JSON.toJSONString("当前登录session用户：" + sessionUserBean));
            if (sessionUserBean == null) {
                return false;
            }
            log.debug("文件所属用户id：" + userFile.getUserId());
            log.debug("登录用户id:" + sessionUserBean.getId());
            if (userFile.getUserId().longValue() != sessionUserBean.getId().longValue()) {
                log.info("用户id不一致，权限校验失败");
                return false;
            }
        } else {
            Map<String, Object> param = new HashMap<>();
            param.put("shareBatchNum", shareBatchNum);
            List<Share> shareList = shareService.listByMap(param);
            //判断批次号
            if (shareList.size() <= 0) {
                log.info("分享批次号不存在，权限校验失败");
                return false;
            }
            Integer shareType = shareList.get(0).getShareType();
            if (1 == shareType) {
                //判断提取码
                if (!shareList.get(0).getExtractionCode().equals(extractionCode)) {
                    log.info("提取码错误，权限校验失败");
                    return false;
                }
            }
            param.put("fileId", fileId);
            // List<ShareFile> shareFileList = shareFileService.listByMap(param);
            // if (shareFileList.size() <= 0) {
            //     log.info("用户id和分享批次号不匹配，权限校验失败");
            //     return false;
            // }

        }
        return true;
    }

}
