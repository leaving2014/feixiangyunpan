package com.fx.pan.controller;

import cn.hutool.core.date.DateUtil;
import com.fx.pan.annotation.Limit;
import com.fx.pan.common.Constants;
import com.fx.pan.domain.Chunk;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.dto.file.UploadFileDTO;
import com.fx.pan.factory.FxUtils;
import com.fx.pan.service.FileService;
import com.fx.pan.service.FileTransferService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.*;
import com.fx.pan.utils.file.convert.FormatConversion;
import com.fx.pan.vo.file.UploadFileVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leaving
 * @date 2021/12/14 9:11
 * @version 1.0
 */

@Slf4j
@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
@RestController
@RequestMapping("/filetransfer")
@RequiredArgsConstructor
@PropertySource(value = {"classpath:application.properties"})
public class FileTransferController {

    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Value("${fx.absoluteCachePath}")
    String absoluteCachePath;

    @Value("${fx.storageType}")
    Integer storageType;

    @Resource
    private FileTransferService fileTransferService;

    @Resource
    private StorageService storageService;

    @Resource
    private FileService fileService;

    @Resource
    private RedisCache redisCache;


    /**
     * 极速上传 根据MD5进行极速上传
     *
     * @return
     */
    // @ApiOperation(value = "极速上传", notes = "根据MD5进行极速上传")
    @GetMapping("/upload")
    public ResponseResult quickUpload(UploadFileDTO uploadFileDto, @ModelAttribute Chunk chunk) {
        Long userId = SecurityUtils.getUserId();
        boolean isCheckSuccess = storageService.checkStorage(userId, uploadFileDto.getTotalSize());
        if (!isCheckSuccess) {
            return ResponseResult.error(500, "存储空间不足");
        }
        ResponseResult ret = new ResponseResult();
        try {
            String identifier = chunk.getIdentifier();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("identifier", identifier);
            List<Chunk> erpChunks =
                    redisCache.getCacheList("upload-file-userId-" + userId + "-" + identifier + "*");
            Set<Integer> chunkNumbers = new HashSet<Integer>();
            for (Chunk erpChunk : erpChunks) {
                chunkNumbers.add(erpChunk.getChunkNumber());
            }
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("chunkNumbers", chunkNumbers);
            params = new HashMap<String, Object>();
            params.put("identifier", identifier);
            List<FileBean> files = fileService.selectFileByIdentifier(identifier);
            if (files.size() > 0) {
                res.put("code", 200);
                res.put("fileName", files.get(0).getFileName());
                res.put("fileUrl", files.get(0).getFileUrl());
                res.put("fileSize", files.get(0).getFileSize());
            } else if (erpChunks.size() > 0 && erpChunks.size() == erpChunks.get(0).getTotalChunks()) {
                res.put("message", "上传成功！");
                res.put("code", 205);
            }
            ret.setData(res);
        } catch (Exception e) {
            ret.setCode(500);
            ret.setMsg("操作失败:" + e.getMessage());
            e.printStackTrace();
        }
        UploadFileVo uploadFileVo = fileTransferService.uploadFileSpeed(uploadFileDto);
        return ResponseResult.success(uploadFileVo);
    }

    /**
     * 处理文件上传POST请求
     * 将上传的文件存放到服务器内
     *
     * @param chunk    文件块
     * @param response 响应
     * @return 上传响应状态
     */
    @PostMapping("/upload")
    public ResponseResult uploadPost(@ModelAttribute Chunk chunk,
                                     HttpServletResponse response) {
        return fileTransferService.fileUploadPost(chunk, response, chunk.getFilePath(), "/");
    }



    @GetMapping("/download")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam String id,
                             @RequestParam Long uid, @RequestParam Long fid, @RequestParam int count) throws UnsupportedEncodingException {
        FileBean fileBean = fileService.selectFileById(fid);
        String filePath = FileUtils.getLocalStorageFilePathByFileBean(fileBean);
        File file = new File(filePath);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            ServletContext context = request.getServletContext();
            // get MIME type of the file
            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());
            // 设置文件名
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileBean.getFileName().getBytes("utf-8"),
                            "ISO8859-1"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally { // 做关闭操作
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @GetMapping("/download/batch")
    public void downloadBatchFile(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam String filePath,
                                  @RequestParam String fileList,@RequestParam("t") Long timestamp) throws UnsupportedEncodingException {
        String s = Base64Util.decodeBase64(fileList);
        String[] list = s.split(",");
        // System.out.println(list);
        String zipFileName = "";
        List<File> files = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            FileBean fileBean = fileService.selectFileById(Long.parseLong(list[i]));
            String path = FileUtils.getLocalStorageFilePathByFileBean(fileBean);
            File file = new File(path);
            files.add(file);
            // System.out.println(path);
        }
        String fileName = timestamp+".zip";
        zipFileName = absoluteCachePath+timestamp+".zip";
        boolean zipFlag = false;

        try {
            zipFlag = FileUtils.doCompressFiles(files, new File(zipFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (zipFlag) {
            File file = new File(zipFileName);
            response.setContentType("application/force-download");// 设置强制下载不打开
            ServletContext context = request.getServletContext();
            // get MIME type of the file
            String mimeType = context.getMimeType(filePath);
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";

            }
            // set content attributes for the response
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());
            // 设置文件名
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));

            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally { // 做关闭操作
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            file.delete();
        }


        // File file = new File(zipFileName);
        // file.delete();
    }


    /**
     * 获取原图
     *
     * @param time                时间戳
     * @param id                  文件id
     * @param extensionName       文件扩展名
     * @return
     */
    @RequestMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] image(Long time, String id, String fileType, String extensionName,HttpServletResponse response) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);
        String filePath = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        File file = new File(filePath);
        FileInputStream inputStream = null;
        // 设置响应头
        response.setHeader("Cache-Control", "max-age=2592000");
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[0];
        try {
            bytes = new byte[inputStream.available()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.read(bytes, 0, inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;

    }

    /**
     * 获取文件预览
     *
     * @param time                时间戳
     * @param id                  文件id
     * @param extensionName       文件扩展名
     * @return
     */
    @SneakyThrows
    @RequestMapping(value = "/preview", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] preview(String time, String id, int fileType, String extensionName,HttpServletResponse response) {
        Long identity = Long.valueOf(time);
        Date d = new Date(identity);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);
        String filePath;
        byte[] bytes = new byte[0];
        // 设置响应头
        response.setHeader("Cache-Control", "max-age=2592000");
        // 图片类型预览
        if (fileType == 1) {
            filePath = absoluteCachePath + "/" + date + "/" + id + "." + extensionName;
            File file = new File(filePath);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                bytes = new byte[inputStream.available()];
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.read(bytes, 0, inputStream.available());
            } catch (IOException e) {
                e.printStackTrace();

            }
        } else if (fileType == 2) {
            filePath = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
            File file = new File(filePath);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                bytes = new byte[inputStream.available()];
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.read(bytes, 0, inputStream.available());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }


    @RequestMapping(value = "/preview/document")
    @ResponseBody
    public byte[] previewDocument(HttpServletResponse response,
                                  Long time, String id, int fileType, String extensionName) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);

        String filePath;
        byte[] bytes = new byte[0];
        response.setHeader("Accept-Ranges", "bytes");
        filePath = FxUtils.getStaticPath() + "\\" + date + "\\" + id + "." + extensionName;
        File file1 = new File(filePath);
        if (!file1.exists()) {
            throw new RuntimeException("文件预览失败");
        }
        File file = new File(filePath);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            bytes = new byte[inputStream.available()];
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.read(bytes, 0, inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }


    /**
     * 音视频流
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/preview/stream", method = RequestMethod.GET)
    public void player(HttpServletRequest request, HttpServletResponse response, Long time, String id, int fileType,
                       String extensionName) {
        BufferedInputStream bis = null;
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);
        String path = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        try {
            File file = new File(path);
            if (file.exists()) {
                long fileLength = file.length();
                // 随机读文件
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                //获取从那个字节开始读取文件
                String rangeString = request.getHeader("Range");
                long range = 0;
                if (rangeString != null) {
                    range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
                }
                //获取响应的输出流
                OutputStream outputStream = response.getOutputStream();
                //设置内容类型
                response.setContentType("application/octet-stream");
                //返回码需要为206，代表只处理了部分请求，响应了部分数据
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                // 移动访问指针到指定位置
                randomAccessFile.seek(range);
                // 每次请求只返回1MB的视频流
                byte[] bytes = new byte[1024 * 1024 * 3];
                int len = randomAccessFile.read(bytes);
                //设置此次相应返回的数据长度
                response.setContentLength(len);
                //设置此次相应返回的数据范围
                response.setHeader("Content-Range", "bytes " + range + "-" + (fileLength - 1) + "/" + fileLength);
                // 将这3MB的视频流响应给客户端
                outputStream.write(bytes, 0, len);
                outputStream.close();
                randomAccessFile.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件格式转换
     * @param fileExt    待转换文件格式
     * @param convertExt 转换文件格式
     * @param fileId     文件id
     * @return
     */

    @PostMapping("/formatconversion")
    @Limit(key = "limit2", permitsPerSecond = 0.2, timeout = 500, msg =
            "请求过于频繁，请稍后再试！")
    public ResponseResult formatConversion(@RequestParam String fileExt, @RequestParam String convertExt,
                                           @RequestParam Long fileId,@RequestParam Long t) throws FileNotFoundException {
        String type = "conversion";
        Long userId = SecurityUtils.getUserId();
        String[] doc = {"doc", "docx"};
        String[] xls = {"xls", "xlsx"};
        String[] ppt = {"ppt", "pptx"};
        Boolean flag = false;
        Long fileSize = 0L;
        FileBean fileBean = fileService.selectFileById(fileId);
        FileBean newFileBean = new FileBean();
        String orginFilePath = FxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
        String convertFilePath = absoluteFilePath + "/tmp/" + fileBean.getFileName().replace(fileBean.getFileExt(),
                convertExt);

        redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId + "-file" +
                        ":" + t,
                fileBean);
        redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                        ":" + t,
                0);
        if (Arrays.asList(doc).contains(fileExt)) {
            if ("pdf".equals(convertExt)) {
                FormatConversion.doc2pdf(orginFilePath, convertFilePath,fileBean,userId,type,t);
                return ResponseResult.success("Word文档转PDF文件任务创建成功！");
            }
        } else if (Arrays.asList(xls).contains(fileExt)) {
            FormatConversion.excel2pdf(orginFilePath, convertFilePath);
            return ResponseResult.success("xls文件转PDF文件任务创建成功！");
        } else if ("pdf".equals(fileExt)) {
            FormatConversion.pdf2Doc(orginFilePath, convertFilePath,fileBean,userId,type,t);
            return ResponseResult.success("PDF文件转Word文档任务创建成功！");
        }
        if (flag) {
            Date date = new Date();
            String dateStr = DateUtil.format(date, "yyyyMMdd");
            File convertFile = new File(convertFilePath);
            String md5 = Md5Utils.md5HashCode32(convertFilePath);
            String fileUrl = dateStr + "/" + md5 + "." + convertExt;
            newFileBean = BeanCopyUtils.copyBean(FileUtils.getFileBeanByPath(convertFilePath, fileBean.getFilePath(),
                    date, storageType, userId), FileBean.class);
            fileService.save(newFileBean);
            redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId + "-file"+
                    ":" + t, newFileBean);
            fileSize = newFileBean.getFileSize();
            boolean b = storageService.updateStorageUse(fileSize, userId);
            // 移动文件到文件存储路径
            File moveFolder = new File(com.fx.pan.factory.FxUtils.getStaticPath() + "/" + dateStr);
            // 判断目录moveFolder是否存在，不存在则创建
            if (!moveFolder.exists()) {
                moveFolder.mkdirs();
            }
            convertFile.renameTo(new File(com.fx.pan.factory.FxUtils.getStaticPath() + "/" + fileUrl));
        }
        if(flag){
            Map<String, Object> map = new HashMap<>();
            map.put("file", newFileBean);
            return ResponseResult.success( "转换成功",map);
        } else {
            return ResponseResult.error(500, "转换失败");
        }
    }

}

