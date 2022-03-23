package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.component.FileDealComp;
import com.fx.pan.domain.Chunk;
import com.fx.pan.domain.FileBean;
import com.fx.pan.dto.file.DownloadFileDTO;
import com.fx.pan.dto.file.UploadFileDTO;
import com.fx.pan.handle.NonStaticResourceHttpRequestHandler;
import com.fx.pan.service.FileService;
import com.fx.pan.service.FileTransferService;
import com.fx.pan.utils.file.Word2PdfAsposeUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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
public class FileTransferController {
    private NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Value("${fx.absoluteCachePath}")
    String absoluteCachePath;

    @Autowired
    private FileTransferService fileTransferService;

    @Autowired
    private FileService fileService;

    @Autowired
    FileDealComp fileDealComp;

    public FileTransferController() {
    }

    // @Autowired
    // private FxFactory fxFactory;


    /**
     * 极速上传 根据MD5进行极速上传
     *
     * @return
     */
    @PostMapping("/quickUpload")
    public Msg quickUpload(UploadFileDTO uploadFileDto, @RequestHeader("Authorization") String token) {

        return Msg.success("");
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
    @PostMapping("/fileUpload")
    public Msg uploadPost(@ModelAttribute Chunk chunk,
                          HttpServletResponse response, @RequestParam(defaultValue = "/") String filePath) {
        return fileTransferService.fileUploadPost(chunk, response, filePath);
    }


    @PostMapping("/uploadpreview")
    public void uploadPreview(@ModelAttribute Chunk chunk, HttpServletResponse response) {

    }

    // https://pan.qiwenshare.com/api/filetransfer/
    // downloadfile?userFileId=80042&shareBatchNum=&extractionCode=
    // https://pan.qiwenshare.com/api/filetransfer/downloadfile?
    // userFileId=80041&shareBatchNum=&extractionCode=
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
    public String downloadFile(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileName
            , @RequestParam Long fileId) {
        // Long fileId= downloadFileDTO.getUserFileId();
        // 文件名
        // String fileName = downloadFileDTO.getFileName();

        FileBean fileBean = fileService.selectFileById(fileId);
        // 如果下载的是单文件
        if (fileBean.getIsDir() == 0) {
            File file = new File("D:\\ideaWorkspace\\pan\\target\\static\\file\\" + fileName);
            //File file = new File(realPath , fileName);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
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
                    return "下载成功";
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

        if (fileName != null) {
            //设置文件路径

        }
        return "下载失败";
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
                          Long time, String id, int fileType, String extensionName) {
        Date d = new Date(time);
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
            System.out.println("filePath:=====" + filePath);
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

        filePath = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;

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
//        String path = request.getServletContext().getRealPath("/static/my/video/interview.mp4");

        BufferedInputStream bis = null;
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(d);
        String path = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;
        // Path filePath = Paths.get(path);
        System.out.println("媒体文件地址====="+path);

        try {
            File file = new File(path);
            if (file.exists()) {
                long p = 0L;
                long toLength = 0L;
                long contentLength = 0L;
                int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=27000-）；2,从某字节开始到某字节结束的下载（bytes=27000-39000）
                long fileLength;
                String rangBytes = "";
                fileLength = file.length();

                // get file content
                InputStream ins = new FileInputStream(file);
                bis = new BufferedInputStream(ins);

                // tell the client to allow accept-ranges
                response.reset();
                response.setHeader("Accept-Ranges", "bytes");

                // client requests a file block download start byte
                String range = request.getHeader("Range");
                if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
                    rangBytes = range.replaceAll("bytes=", "");
                    if (rangBytes.endsWith("-")) { // bytes=270000-
                        rangeSwitch = 1;
                        p = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                        contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
                    } else { // bytes=270000-320000
                        rangeSwitch = 2;
                        String temp1 = rangBytes.substring(0, rangBytes.indexOf("-"));
                        String temp2 = rangBytes.substring(rangBytes.indexOf("-") + 1, rangBytes.length());
                        p = Long.parseLong(temp1);
                        toLength = Long.parseLong(temp2);
                        contentLength = toLength - p + 1; // 客户端请求的是 270000-320000 之间的字节
                    }
                } else {
                    contentLength = fileLength;
                }

                // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
                // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
                response.setHeader("Content-Length", new Long(contentLength).toString());

                // 断点开始
                // 响应的格式是:
                // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                if (rangeSwitch == 1) {
                    String contentRange = new StringBuffer("bytes ").append(new Long(p).toString()).append("-")
                            .append(new Long(fileLength - 1).toString()).append("/")
                            .append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else if (rangeSwitch == 2) {
                    String contentRange = range.replace("=", " ") + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else {
                    String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/")
                            .append(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                }

                String fileName = file.getName();
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);

                OutputStream out = response.getOutputStream();
                int n = 0;
                long readLength = 0;
                int bsize = 1024;
                byte[] bytes = new byte[bsize];
                if (rangeSwitch == 2) {
                    // 针对 bytes=27000-39000 的请求，从27000开始写数据
                    while (readLength <= contentLength - bsize) {
                        n = bis.read(bytes);
                        readLength += n;
                        out.write(bytes, 0, n);
                    }
                    if (readLength <= contentLength) {
                        n = bis.read(bytes, 0, (int) (contentLength - readLength));
                        out.write(bytes, 0, n);
                    }
                } else {
                    while ((n = bis.read(bytes)) != -1) {
                        out.write(bytes, 0, n);
                    }
                }
                out.flush();
                out.close();
                bis.close();
            }
        } catch (IOException ie) {
            // 忽略 ClientAbortException 之类的异常
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
    public Msg formatConversion(@RequestParam String fileExt, @RequestParam String convertExt,
                                @RequestParam Long fileId) {
        Boolean flag = false;
        Integer code = 500;
        String msg = "转换失败";
        FileBean fileBean = fileService.selectFileById(fileId);
        if (fileExt.equals("docx")) {
            if ("pdf".equals(convertExt)) {
                String docPath = absoluteFilePath + fileBean.getFileName();
                String pdfPath = absoluteFilePath + fileBean.getFileName().replace(fileExt, convertExt);
                flag = Word2PdfAsposeUtil.doc2pdf(docPath, pdfPath);
                if (flag) {
                    code = 200;
                    msg = "转换成功";
                }
            }
        } else if ("pdf".equals(fileExt)) {

        }

        return Msg.msg(code, msg);
    }

}

