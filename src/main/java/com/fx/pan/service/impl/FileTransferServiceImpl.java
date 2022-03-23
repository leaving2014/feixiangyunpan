package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.Chunk;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Storage;
import com.fx.pan.dto.file.DownloadFileDTO;
import com.fx.pan.dto.file.PreviewDTO;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.mapper.StorageMapper;
import com.fx.pan.service.FileTransferService;
import com.fx.pan.utils.FileUtil;
import com.fx.pan.utils.Md5Utils;
import com.fx.pan.utils.SecurityUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author leaving
 * @Date 2021/12/14 9:11
 * @Version 1.0
 */
@Slf4j
@Service
@Data
public class FileTransferServiceImpl extends ServiceImpl<FileMapper, FileBean> implements FileTransferService {


    @Value("${fx.fileStorageType}")
    private static Integer fileStorageType;

    @Resource
    private FileMapper fileMapper;

    @Value("${fx.absoluteFilePath}")
    String absolutePath;

    // @Autowired
    // private StorageService storageService;

    @Resource
    private StorageMapper storageMapper;


    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {


        // FileBean userFile = fileMapper.selectById(downloadFileDTO.getUserFileId());
        //
        // if (userFile.getFileIsdir()== "0") {
        //
        //     FileBean fileBean = fileMapper.selectById(userFile.getId());
        //     Downloader downloader = FxFactory.getDownloader(fileBean.getFileType());
        //     if (downloader == null) {
        //         log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
        //         throw new DownloadException("下载失败");
        //     }
        //     DownloadFile downloadFile = new DownloadFile();
        //
        //     downloadFile.setFileUrl(fileBean.getFileMd5());
        //     downloadFile.setFileSize(fileBean.getFileSize());
        //     httpServletResponse.setContentLengthLong(fileBean.getFileSize());
        //     downloader.download(httpServletResponse, downloadFile);
        // } else {
        //     LambdaQueryWrapper<FileBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //     lambdaQueryWrapper.likeRight(FileBean::getFilePath, userFile.getFilePath() + userFile.getFileName() +
        //     "/")
        //             .eq(FileBean::getUserId, userFile.getUserId())
        //             .eq(FileBean::getFileIsdir, "0");
        //     List<FileBean> userFileList = fileMapper.selectList(lambdaQueryWrapper);
        //
        //     String staticPath = fxUtils.getStaticPath();
        //     String tempPath = staticPath + "temp" + File.separator;
        //     File tempDirFile = new File(tempPath);
        //     if (!tempDirFile.exists()) {
        //         tempDirFile.mkdirs();
        //     }
        //
        //     FileOutputStream f = null;
        //     try {
        //         f = new FileOutputStream(tempPath + userFile.getFileName() + ".zip");
        //     } catch (FileNotFoundException e) {
        //         e.printStackTrace();
        //     }
        //     CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
        //     ZipOutputStream zos = new ZipOutputStream(csum);
        //     BufferedOutputStream out = new BufferedOutputStream(zos);
        //
        //     try {
        //         for (FileBean userFile1 : userFileList) {
        //             FileBean fileBean = fileMapper.selectById(userFile1.getId());
        //             Downloader downloader = FxFactory.getDownloader(fileBean.getStorageType());
        //             if (downloader == null) {
        //                 log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
        //                 throw new UploadException("下载失败");
        //             }
        //             DownloadFile downloadFile = new DownloadFile();
        //             downloadFile.setFileUrl(fileBean.getFileMd5());
        //             downloadFile.setFileSize(fileBean.getFileSize());
        //             InputStream inputStream = downloader.getInputStream(downloadFile);
        //             BufferedInputStream bis = new BufferedInputStream(inputStream);
        //             try {
        //                 zos.putNextEntry(new ZipEntry(userFile1.getFilePath().replace(userFile.getFilePath(), "/")
        //                 + userFile1.getFileName() + "." + userFile1.getFileExt()));
        //
        //                 byte[] buffer = new byte[1024];
        //                 int i = bis.read(buffer);
        //                 while (i != -1) {
        //                     out.write(buffer, 0, i);
        //                     i = bis.read(buffer);
        //                 }
        //             } catch (IOException e) {
        //                 log.error("" + e);
        //                 e.printStackTrace();
        //             } finally {
        //                 try {
        //                     bis.close();
        //                 } catch (IOException e) {
        //                     e.printStackTrace();
        //                 }
        //                 try {
        //                     out.flush();
        //                 } catch (IOException e) {
        //                     e.printStackTrace();
        //                 }
        //             }
        //         }
        //     } catch (Exception e) {
        //         log.error("压缩过程中出现异常:"+ e);
        //     } finally {
        //         try {
        //             out.close();
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }
        //     }
        //     String zipPath = "";
        //     try {
        //         Downloader downloader = FxFactory.getDownloader(StorageTypeEnum.LOCAL.getCode());
        //         DownloadFile downloadFile = new DownloadFile();
        //         downloadFile.setFileUrl("temp" + File.separator + userFile.getFileName() + ".zip");
        //         File tempFile = new File(fxUtils.getStaticPath() + downloadFile.getFileUrl());
        //         httpServletResponse.setContentLengthLong(tempFile.length());
        //         downloader.download(httpServletResponse, downloadFile);
        //         zipPath = fxUtils.getStaticPath() + "temp" + File.separator + userFile.getFileName() + ".zip";
        //     } catch (Exception e) {
        //         //org.apache.catalina.connector.ClientAbortException: java.io.IOException: Connection reset by peer
        //         if (e.getMessage().contains("ClientAbortException")) {
        //             //该异常忽略不做处理
        //         } else {
        //             log.error("下传zip文件出现异常：{}", e.getMessage());
        //         }
        //
        //     } finally {
        //         File file = new File(zipPath);
        //         if (file.exists()) {
        //             file.delete();
        //         }
        //     }
        // }
    }

    @Override
    @SneakyThrows
    public Msg fileUploadPost(Chunk chunk, HttpServletResponse response, String filePath) {
        Long userId = SecurityUtils.getUserId();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String formatDate = simpleDateFormat.format(date);
        /**
         * 每一个上传块都会包含如下分块信息：
         * chunkNumber: 当前块的次序，第一个块是 1，注意不是从 0 开始的。
         * totalChunks: 文件被分成块的总数。
         * chunkSize: 分块大小，根据 totalSize 和这个值你就可以计算出总共的块数。注意最后一块的大小可能会比这个要大。
         * currentChunkSize: 当前块的大小，实际大小。
         * totalSize: 文件总大小。
         * identifier: 这个就是每个文件的唯一标示。
         * filename: 文件名。
         * relativePath: 文件夹上传的时候文件的相对路径属性。
         * 一个分块可以被上传多次，当然这肯定不是标准行为，但是在实际上传过程中是可能发生这种事情的，这种重传也是本库的特性之一。
         *
         * 根据响应码认为成功或失败的：
         * 200 文件上传完成
         * 201 文加快上传成功
         * 500 第一块上传失败，取消整个文件上传
         * 507 服务器出错自动重试该文件块上传
         */

        // 打包后防止路径找不到
        // ApplicationHome h = new ApplicationHome(getClass());
        // File jarF = h.getSource();
        // System.out.println("jarF.getAbsolutePath()"+jarF.getAbsolutePath());
        // String staticPath = jarF.getParentFile().toString();
        // System.out.println("staticPath:"+staticPath);


        // staticPathD:\ideaWorkspace\pan\target
        // System.out.println("PathUtils.getFileDir():"+PathUtils.getFileDir());

        File folder = new File(absolutePath + "/" + formatDate);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }

        File file = new File(absolutePath + "/" + formatDate, chunk.getIdentifier()+"."+FileUtil.getFileExt(chunk.getFilename()));
        // File file= new File(PathUtils.getFileDir(), chunk.getFilename());
        //第一个块,则新建文件

        if (chunk.getChunkNumber() == 1 && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                response.setStatus(500);
                return Msg.error(500, "创建文件异常");
            }
        }

        //进行写文件操作
        try (
                //将块文件写入文件中
                InputStream fos = chunk.getFile().getInputStream();
                RandomAccessFile raf = new RandomAccessFile(file, "rw")
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
            return Msg.error(507, "写出文件异常");
        }
        if (chunk.getChunkNumber().equals(chunk.getTotalChunks())) {
            response.setStatus(200);
            // TODO 向数据库中保存上传信息
            System.out.println("storageType:" + fileStorageType);
            // System.out.println(Integer.valueOf(storageType));

            FileBean f = FileUtil.getFileBean(chunk, date, fileStorageType, userId);
            fileMapper.insert(f);

            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id", userId);
            Storage storage = storageMapper.selectOne(queryWrapper);
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("storage_size_used", chunk.getTotalSize() + storage.getStorageSizeUsed());
            updateWrapper.eq("user_id", userId);
            storageMapper.update(storage, updateWrapper);

            return Msg.success("上传成功").put("file", JSONObject.toJSON(f));
        } else {
            response.setStatus(201);
            MultipartFile file1 = chunk.getFile();
            String md5 = Md5Utils.md5HashCode32(new ByteArrayInputStream(file1.getBytes()));
            return Msg.success("ok").put("md5", md5);
        }
    }

    @Override
    public void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        // FileBean userFile = fileMapper.selectById(previewDTO.getFileId());
        // FileBean fileBean = fileMapper.selectById(userFile.getId());
        // Previewer previewer = fxFactory.getPreviewer(fileBean.getStorageType());
        // if (previewer == null) {
        //     log.error("预览失败，文件存储类型不支持预览，storageType:{}", fileBean.getStorageType());
        //     throw new UploadException("预览失败");
        // }
        // PreviewFile previewFile = new PreviewFile();
        // previewFile.setFileUrl(fileBean.getFilePath());
        // previewFile.setFileSize(fileBean.getFileSize());
        // try {
        //     if ("true".equals(previewDTO.getIsMin())) {
        //         previewer.imageThumbnailPreview(httpServletResponse, previewFile);
        //     } else {
        //         previewer.imageOriginalPreview(httpServletResponse, previewFile);
        //     }
        // } catch (Exception e){
        //     //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
        //     if (e.getMessage().contains("ClientAbortException")) {
        //         //该异常忽略不做处理
        //     } else {
        //         log.error("预览文件出现异常：{}", e.getMessage());
        //     }
        //
        // }
    }
}
