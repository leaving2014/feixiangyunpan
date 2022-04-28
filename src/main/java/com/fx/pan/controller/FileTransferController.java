package com.fx.pan.controller;

import cn.hutool.core.date.DateUtil;
import com.fx.pan.annotation.Limit;
import com.fx.pan.common.Msg;
import com.fx.pan.component.FileDealComp;
import com.fx.pan.domain.Chunk;
import com.fx.pan.domain.FileBean;
import com.fx.pan.dto.file.DownloadFileDTO;
import com.fx.pan.dto.file.UploadFileDTO;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.handle.NonStaticResourceHttpRequestHandler;
import com.fx.pan.service.FileService;
import com.fx.pan.service.FileTransferService;
import com.fx.pan.service.StorageService;
import com.fx.pan.utils.*;
import com.fx.pan.utils.file.Word2PdfAsposeUtil;
import com.fx.pan.utils.file.convert.FormatConversion;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author leaving
 * @Date 2021/12/14 9:11
 * @Version 1.0
 */

@Slf4j
@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
@RestController
@RequestMapping("/filetransfer")
@AllArgsConstructor
@PropertySource(value = {"classpath:application.properties"})
public class FileTransferController {
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Value("${fx.absoluteCachePath}")
    String absoluteCachePath;

    @Value("${fx.storageType}")
    Integer storageType;
    @Autowired
    private FileTransferService fileTransferService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileService fileService;

    @Autowired
    FileDealComp fileDealComp;

    @Autowired
    private RedisCache redisCache;

    public FileTransferController() {
    }

    /**
     * 极速上传 根据MD5进行极速上传
     *
     * @return
     */
    @GetMapping("/upload")
    public Msg quickUpload(UploadFileDTO uploadFileDto, @ModelAttribute Chunk chunk) {
        Long userId = SecurityUtils.getUserId();
        boolean isCheckSuccess = storageService.checkStorage(userId, uploadFileDto.getTotalSize());
        if (!isCheckSuccess) {
            return Msg.msg(500, "存储空间不足");
        }
        Msg ret = new Msg();
        try {
            String identifier = chunk.getIdentifier();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("identifier", identifier);
            // Collection<T> collection = redisCache.keys("upload-file-userId-" + userId + "-" + identifier + "*");
            List<Chunk> erpChunks =
                    redisCache.getCacheList("upload-file-userId-" + userId + "-" + identifier + "*");
            // List<T> erpChunks =new ArrayList<T>(collection);
            // 将erpChunks转换为List<Chunk>
            // List<Chunk> chunks = erpChunks.stream().map(t -> (Chunk) t).collect(Collectors.toList());
            // search(params, Chunk.class);
            // chunkService.find(params);
            Set<Integer> chunkNumbers = new HashSet<Integer>();
            for (Chunk erpChunk : erpChunks) {
                chunkNumbers.add(erpChunk.getChunkNumber());
            }
            System.out.println("chunkNumbers===:" + chunkNumbers);
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("chunkNumbers", chunkNumbers);
            params = new HashMap<String, Object>();
            params.put("identifier", identifier);
            List<FileBean> files = fileService.selectFileByIdentifier(identifier);
            // redisCache.getCacheList("upload-file-userId-"+ userId+"-"+identifier+"-"+ chunkNumbers);
            // keys("upload-file-userId-"+ userId+"-"+identifier+"*");
            // fileService.find(params);
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

        // UploadFileVo uploadFileVo = fileTransferService.uploadFileSpeed(uploadFileDto);
        // return Msg.success("极速上传成功").put("file", uploadFileVo);
        return ret;
    }

    /**
     * 处理文件上传POST请求
     * 将上传的文件存放到服务器内
     *
     * @param chunk    文件块
     * @param response 响应
     * @return 上传响应状态
     */
    // @PreAuthorize("has")
    @PostMapping("/upload")
    public Msg uploadPost(@ModelAttribute Chunk chunk,
                          HttpServletResponse response) {
        // @RequestParam(defaultValue = "/") String filePath,
        // @RequestParam String relativePath
        return fileTransferService.fileUploadPost(chunk, response, chunk.getFilePath(), "/");
        // return fileTransferService.fileUpload(chunk, response);
    }


    @PostMapping("/uploadpreview")
    public void uploadPreview(@ModelAttribute Chunk chunk, HttpServletResponse response) {

    }

    @GetMapping("/download11")
    public void download(HttpServletResponse httpServletResponse, @RequestBody DownloadFileDTO downloadFileDTO) {
        // File file = new File(downloadFileDTO.getExtractionCode());
        //
        // return Msg.success("");

        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        FileBean userFile = fileService.getById(downloadFileDTO.getUserFileId());
        String fileName = "";
        if (userFile.getIsDir() == 1) {
            fileName = "批量下载" + userFile.getFileName() + ".zip";
        } else {
            fileName = userFile.getFileName() + "." + userFile.getFileExt();

        }
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名

        fileTransferService.downloadFile(httpServletResponse, downloadFileDTO);
    }

    // http://localhost:8080/api/filetransfer/download?fileName=folder.png
    @GetMapping("/download")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam String id,
                             @RequestParam Long uid, @RequestParam Long fid, @RequestParam int count) throws UnsupportedEncodingException {
        // Long fileId= downloadFileDTO.getUserFileId();
        // 文件名
        // String fileName = downloadFileDTO.getFileName();

        FileBean fileBean = fileService.selectFileById(fid);
        // 如果下载的是单文件
        if (fileBean.getIsDir() == 0) {
            String filePath = FileUtils.getLocalStorageFilePathByFileBean(fileBean);
            File file = new File(filePath);
            //File file = new File(realPath , fileName);
            if (file.exists()) {
                // if (download) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                // }
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
        } else {
            // 如果下载的是多文件和目录

        }

        // if (fileName != null) {
        //     //设置文件路径
        //
        // }
        // return "下载失败";
    }


    @GetMapping("/download/batch")
    public void downloadBatchFile(HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam String filePath,
                                  @RequestParam String fileList) throws UnsupportedEncodingException {
        String s = SecretUtil.decodeBase64(fileList);
        String[] list = s.split(",");
        System.out.println(list);

        List<File> files = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            FileBean fileBean = fileService.selectFileById(Long.parseLong(list[i]));
            String path = FileUtils.getLocalStorageFilePathByFileBean(fileBean);
            File file = new File(path);

            files.add(file);
            System.out.println(path);
        }
        String fileName = "下载.zip";
        String zipFileName = "D:/ideaWorkspace/pan/static/cache/下载.zip";
        // FileUtils.createDownloadZipFile(zipFileName);
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
        }


    }

    ;


    @GetMapping("/rowfile")
    public void rowFile(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam String id,
                        @RequestParam Long uid, @RequestParam Long fid) {


    }

    /**
     * 获取原图
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param time                时间戳
     * @param id                  文件id
     * @param extensionName       文件扩展名
     * @return
     */
    @RequestMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] image(HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse,
                        Long time, String id, String fileType, String extensionName) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);
        String filePath = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        File file = new File(filePath);
        FileInputStream inputStream = null;
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
     * @param httpServletRequest
     * @param httpServletResponse
     * @param time                时间戳
     * @param id                  文件id
     * @param extensionName       文件扩展名
     * @return
     */
    @SneakyThrows
    @RequestMapping(value = "/preview", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] preview(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                          String time, String id, int fileType, String extensionName) {
        Long identity = Long.valueOf(time);
        Date d = new Date(identity);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);
        // FileBean userFile = fileService.getById(previewDTO.getFileId());
        // boolean authResult = fileDealComp.checkAuthDownloadAndPreview(previewDTO.getShareBatchNum(),
        //         previewDTO.getExtractionCode(),
        //         previewDTO.getToken(),
        //         previewDTO.getFileId());
        //
        // if (!authResult) {
        //     log.error("没有权限预览！！！");
        //     return;
        // }

        // FileBean fileBean = fileService.getById(userFile.getId());
        // String mime= MimeUtils.getMime(userFile.getFileExt());
        // httpServletResponse.setHeader("Content-Type", mime);
        // String rangeString = httpServletRequest.getHeader("Range");//如果是video标签发起的请求就不会为null
        // if (StringUtils.isNotEmpty(rangeString)) {
        //     long range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
        //     httpServletResponse.setContentLength(Math.toIntExact(fileBean.getFileSize()));
        //     httpServletResponse.setHeader("Content-Range", String.valueOf(range + (Math.toIntExact(fileBean
        //     .getFileSize()) - 1)));
        // }
        // httpServletResponse.setHeader("Accept-Ranges", "bytes");
        //
        // String fileName = userFile.getFileName() + "." + userFile.getFileExt();
        // try {
        //     fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        // } catch (UnsupportedEncodingException e) {
        //     e.printStackTrace();
        // }
        //
        // httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名
        //
        // fileTransferService.previewFile(httpServletResponse, previewDTO);


        /**
         * 以前自己的
         */
        // TODO


        // Long userId = SecurityUtils.getUserId();
        String filePath;
        byte[] bytes = new byte[0];
        // 图片类型预览
        if (fileType == 1) {
            // String mime= MimeUtils.getMime(extensionName);
            // httpServletResponse.setHeader("Content-Type", mime);
            // httpServletRequest.setAttribute("produces",MediaType.IMAGE_JPEG_VALUE);
            // produces = MediaType.IMAGE_JPEG_VALUE
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
            System.out.println(filePath);
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
            // InputStream pngInStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            // ByteArrayOutputStream out = new ByteArrayOutputStream();
            // byte[] buffer = new byte[1024];
            // int n;
            // while ((n = pngInStream.read(buffer)) != -1) {
            //     out.write(buffer,0,n);
            // }
            // System.out.println(out.toByteArray());
            // bytes = out.toByteArray();

        }

        return bytes;
    }


    @RequestMapping(value = "/preview/document")
    @ResponseBody
    public byte[] previewDocument(HttpServletRequest request, HttpServletResponse response,
                                  Long time, String id, int fileType, String extensionName) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);

        String filePath;
        byte[] bytes = new byte[0];
        response.setHeader("Accept-Ranges", "bytes");

        // filePath = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        System.out.println("fxUtils.getStaticPath()====" + fxUtils.getStaticPath());
        filePath = fxUtils.getStaticPath() + "\\" + date + "\\" + id + "." + extensionName;
        System.out.println("文档预览路径===" + filePath);
        // filePath = fxUtils.getStaticPath() + writeFile.getFileUrl();

        File file1 = new File(filePath);
        if (!file1.exists()) {
            System.out.println("234342234");
            // return Msg.error(500, "文预览失败");
            throw new RuntimeException("文件预览失败");
        }
        System.out.println(filePath);
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


    @RequestMapping(value = "/preview/media")
    public void previewMedia(HttpServletRequest request, HttpServletResponse response,
                             Long time, String id, int fileType, String extensionName) throws ServletException,
            IOException {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);


        String path = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        Path filePath = Paths.get(path);
        System.out.println("path:====" + path);
        // filePath = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        if (Files.exists(filePath)) {
            String mimeType = null;
            try {
                mimeType = Files.probeContentType(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!StringUtils.isEmpty(mimeType)) {
                response.setContentType(mimeType);
            }
            request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, filePath);
            nonStaticResourceHttpRequestHandler.handleRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }



    /**
     * 音视频流
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/preview/stream", method = RequestMethod.GET)
    public void player(HttpServletRequest request, HttpServletResponse response,Long time, String id, int fileType, String extensionName) {
        BufferedInputStream bis = null;
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);
        String path = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        try {
            File file = new File(path);
            if (file.exists()) {

            }
                long fileLength = file.length();
            // 随机读文件
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

            //获取从那个字节开始读取文件
            String rangeString = request.getHeader("Range");
            System.out.println(rangeString);
            long range=0;
            if (rangeString!=null) {
                range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
            }
            //获取响应的输出流
            OutputStream outputStream = response.getOutputStream();
            //设置内容类型
            // if (fileType == 3) {
            //     response.setHeader("Content-Type", "video/mp4");
            // } else if (fileType == 5) {
            //     response.setHeader("Content-Type", "audio/mp3");
            // }
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
            response.setHeader("Content-Range", "bytes "+range+"-"+(fileLength-1)+"/"+fileLength);
            // 将这3MB的视频流响应给客户端
            outputStream.write(bytes, 0, len);
            outputStream.close();
            randomAccessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件格式转换
     *
     * @param fileExt    待转换文件格式
     * @param convertExt 转换文件格式
     * @param fileId     文件id
     * @return
     */
    @PostMapping("/formatconversion")
    @Limit(key = "limit2", permitsPerSecond = 0.2, timeout = 500, msg =
            "请求过于频繁，请稍后再试！请求频率限制在0.2次/秒！")
    public Msg formatConversion(@RequestParam String fileExt, @RequestParam String convertExt,
                                @RequestParam Long fileId) throws FileNotFoundException {
        Long userId = SecurityUtils.getUserId();
        String[] doc = {"doc", "docx"};
        String[] xls = {"xls", "xlsx"};
        ;
        String[] ppt = {"ppt", "pptx"};
        Boolean flag = false;
        Integer code = 500;
        Long fileSize = 0L;
        String msg = "转换失败";
        FileBean fileBean = fileService.selectFileById(fileId);
        FileBean newFileBean = new FileBean();


        String orginFilePath = fxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
        String convertFilePath = absoluteFilePath + "/tmp/" + fileBean.getFileName().replace(fileBean.getFileExt(),
                convertExt);


        // 判断doc是否包含fileExt
        if (Arrays.asList(doc).contains(fileExt)) {
            if ("pdf".equals(convertExt)) {
                flag = Word2PdfAsposeUtil.doc2pdf(orginFilePath, convertFilePath);
            }
        } else if (Arrays.asList(xls).contains(fileExt)) {
            flag = FormatConversion.excel2pdf(orginFilePath, convertFilePath);
        } else if ("pdf".equals(fileExt)) {
            flag = FormatConversion.Pdf2Doc(orginFilePath, convertFilePath);
        }

        // 剥离重复代码
        if (flag) {
            Date date = new Date();
            String dateStr = DateUtil.format(date, "yyyyMMdd");
            File convertFile = new File(convertFilePath);
            String md5 = Md5Utils.md5HashCode32(convertFilePath);
            String fileUrl = dateStr + "/" + md5 + "." + convertExt;
            newFileBean = BeanCopyUtils.copyBean(FileUtils.getFileBeanByPath(convertFilePath, fileBean.getFilePath(),
                    date, storageType, userId), FileBean.class);
            fileService.save(newFileBean);
            fileSize = newFileBean.getFileSize();
            boolean b = storageService.updateStorageUse(fileSize, userId);
            // 移动文件到文件存储路径
            File moveFolder = new File(fxUtils.getStaticPath() + "/" + dateStr);
            System.out.println("moveFolder:" + moveFolder.getAbsolutePath());
            // 判断目录moveFolder是否存在，不存在则创建
            if (!moveFolder.exists()) {
                moveFolder.mkdirs();
            }
            convertFile.renameTo(new File(fxUtils.getStaticPath() + "/" + fileUrl));
            code = 0;
            msg = "转换成功";
        }

        return Msg.msg(code, msg).put("file", newFileBean);
    }

}

