package com.fx.pan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.common.Constants;
import com.fx.pan.domain.*;
import com.fx.pan.dto.file.UploadFileDTO;
import com.fx.pan.factory.FxFactory;
import com.fx.pan.factory.FxUtils;
import com.fx.pan.factory.constant.UploadFileStatusEnum;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.mapper.StorageMapper;
import com.fx.pan.service.CosFileService;
import com.fx.pan.service.FileService;
import com.fx.pan.service.FileTransferService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.DateUtil;
import com.fx.pan.utils.FileUtils;
import com.fx.pan.utils.RedisCache;
import com.fx.pan.utils.SecurityUtils;
import com.fx.pan.vo.file.UploadFileVo;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leaving
 * @date 2021/12/14 9:11
 * @version 1.0
 */
@Slf4j
@Service
@Data
public class FileTransferServiceImpl extends ServiceImpl<FileMapper, FileBean> implements FileTransferService {


    @Value("${fx.storageType}")
    Integer storageType;

    @Value("${fx.absoluteFilePath}")
    String absolutePath;

    @Value("${fx.fileAudit}")
    String fileAudit;

    @Resource
    private FileService fileService;
    
    @Resource
    private CosFileService cosFileService;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private StorageService storageService;

    @Autowired
    private FxFactory fxFactory;



    @Resource
    private StorageMapper storageMapper;

    @Autowired
    private RedisCache redisCache;





    /**
     * ????????????
     * @param uploadFileDTO
     * @return
     */
    @Override
    public UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDTO) {
        Long userId = SecurityUtils.getUserId();
        UploadFileVo uploadFileVo = new UploadFileVo();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Map<String, Object> param = new HashMap<>();
        param.put("identifier", uploadFileDTO.getIdentifier());
        List<FileBean> list = fileMapper.selectByMap(param);
        if (list != null && !list.isEmpty()) {
            FileBean fileBean = list.get(0);
            fileBean.setUserId(userId);
            String relativePath = uploadFileDTO.getRelativePath();
            if (relativePath.contains("/")) {
                fileBean.setFilePath(uploadFileDTO.getFilePath() + com.fx.pan.factory.FxUtils.getParentPath(relativePath) + "/");
            } else {
                fileBean.setFilePath(uploadFileDTO.getFilePath());
            }

            String fileName = uploadFileDTO.getFilename();
            fileBean.setFileName(com.fx.pan.factory.FxUtils.getFileNameNotExtend(fileName));
            fileBean.setFileExt(FileUtils.getFileExtendName(fileName));
            fileBean.setDeleted(0);
            Map<String, Object> map = new HashMap<>();
            map.put("file_name", fileName);
            map.put("user_id", fileBean.getUserId());
            List<FileBean> userFileList = fileMapper.selectByMap(map);
            System.out.println("userFileList====" + userFileList);
            if (userFileList.size() <= 0) {
                fileBean.setIsDir(0);
                fileBean.setFileUpdateTime(new Date());
                fileBean.setFileCreateTime(new Date());
                fileMapper.insert(fileBean);
            }

            uploadFileVo.setSkipUpload(true);

        } else {
            log.info("???????????????????????????");
            uploadFileVo.setSkipUpload(false);
            Collection<T> keys =
                    redisCache.keys(Constants.REDIS_UPLOAD_PREFIX + uploadFileDTO.getIdentifier() + ":" + "*");
            log.info("keys={}", keys);
            if (keys != null && !keys.isEmpty()) {
                uploadFileVo.setUploaded(Collections.singletonList(keys.size()));
            } else {

                LambdaQueryWrapper<UploadTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTask::getIdentifier, uploadFileDTO.getIdentifier());
                List<UploadTask> rslist =
                        redisCache.getCacheObject(Constants.REDIS_UPLOAD_PREFIX + uploadFileDTO.getIdentifier() + ":" + uploadFileDTO.getChunkNumber());
                log.info("rslist={}", rslist);
                if (rslist == null || rslist.isEmpty()) {

                    UploadTask uploadTask = new UploadTask();
                    uploadTask.setIdentifier(uploadFileDTO.getIdentifier());
                    uploadTask.setUploadTime(DateUtil.getCurrentTime());
                    uploadTask.setUploadStatus(UploadFileStatusEnum.UNCOMPLATE.getCode());
                    uploadTask.setFileName(uploadFileDTO.getFilename());
                    String relativePath = uploadFileDTO.getRelativePath();
                    if (relativePath.contains("/")) {
                        uploadTask.setFilePath(uploadFileDTO.getFilePath() + FxUtils.getParentPath(relativePath) + "/");
                    } else {
                        uploadTask.setFilePath(uploadFileDTO.getFilePath());
                    }
                    uploadTask.setExtendName(uploadTask.getExtendName());
                    uploadTask.setUserId(loginUser.getUserId());
                    redisCache.set(Constants.REDIS_UPLOAD_PREFIX + uploadFileDTO.getIdentifier() + ":" + uploadFileDTO.getChunkNumber(),
                            uploadTask,
                            60 * 60 * 24);
                }
            }

        }
        return uploadFileVo;
    }


    @Override
    @SneakyThrows
    public ResponseResult fileUploadPost(Chunk chunk, HttpServletResponse response, String filePath,
                                         String relativePath) {
        Long userId = SecurityUtils.getUserId();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String formatDate = simpleDateFormat.format(new Date());
        /**
         * ???????????????????????????????????????????????????
         * chunkNumber: ???????????????????????????????????? 1?????????????????? 0 ????????????
         * totalChunks: ??????????????????????????????
         * chunkSize: ????????????????????? totalSize ?????????????????????????????????????????????????????????????????????????????????????????????????????????
         * currentChunkSize: ????????????????????????????????????
         * totalSize: ??????????????????
         * identifier: ??????????????????????????????????????????
         * filename: ????????????
         * relativePath: ??????????????????????????????????????????????????????
         * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         *
         * ??????????????????????????????????????????
         * 200 ??????????????????
         * 201 ?????????????????????
         * 500 ????????????????????????????????????????????????
         * 507 ?????????????????????????????????????????????
         */

        File folder = new File(absolutePath + "/" + formatDate);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        String uploadFilePath = FileUtils.getLocalStorageFilePath(absolutePath, formatDate,
                chunk.getIdentifier() + "." + FileUtils.getFileExt(chunk.getFilename()));
        File file = new File(uploadFilePath);
        if (chunk.getChunkNumber() == 1 && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                response.setStatus(500);
                return ResponseResult.error(500, "??????????????????");
            }
        }
        //?????????????????????
        try (
                //???????????????????????????
                InputStream fos = chunk.getFile().getInputStream();
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
        ) {
            int len = -1;
            byte[] buffer = new byte[1024];
            raf.seek((chunk.getChunkNumber() - 1) * 1024 * 1024);
            while ((len = fos.read(buffer)) != -1) {
                raf.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (chunk.getChunkNumber() == 1) {
                file.delete();
            }
            response.setStatus(507);
            return ResponseResult.error(507, "??????????????????");
        }
        if (chunk.getChunkNumber().equals(chunk.getTotalChunks())) {
            response.setStatus(200);
            FileBean fileBean = FileUtils.getUploadFileBean(chunk, date, storageType, userId);
            if (fileAudit.equals("1")) {
                if (fileBean.getFileType() == 1) {
                    ImageAuditingResponse res = cosFileService.fileAudit(fileBean,false);
                    fileBean.setAudit(1);
                }
            }
            if (fileBean.getFilePath().equals("/")) {
                fileBean.setParentPathId(-1L);
            } else {
                fileBean.setParentPathId(fileService.selectParentPath(fileBean.getFilePath(),"",userId).getId());
            }
            fileMapper.insert(fileBean);
            storageService.updateStorageUse(chunk.getTotalSize(), userId);
            LambdaQueryWrapper<Storage> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(Storage::getUserId, userId);
            Storage storage = storageMapper.selectOne(queryWrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("storage", storage);
            redisCache.deleteObject(Constants.REDIS_UPLOAD_PREFIX + chunk.getIdentifier() + ":" + chunk.getChunkNumber());
            return ResponseResult.success("????????????", map);
        } else {
            response.setStatus(201);
            return ResponseResult.success("ok");
        }
    }

}
