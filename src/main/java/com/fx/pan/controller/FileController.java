package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.cos.CosClient;
import com.fx.pan.cos.fileUpload;
import com.fx.pan.domain.FileBean;
import com.fx.pan.utils.Md5Utils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

/**
 * @Author leaving
 * @Date 2021/11/24 22:24
 * @Version 1.0
 */
@RequestMapping(value = "/file")
@RestController
@Slf4j
public class FileController {


    @Value("${spring.tengxun.cos.accessKey}")
    private String accessKey;
    @Value("${spring.tengxun.cos.secretKey}")
    private String secretKey;
    @Value("${spring.tengxun.cos.bucketRegion}")
    private String bucketRegion;
    @Value("${spring.tengxun.cos.bucketName}")
    private String bucketName;
    @Value("${spring.tengxun.cos.path}")
    private String path;
    @Value("${spring.tengxun.cos.qianzui}")
    private String qianzui;

    // @Value("${fx.fileStorageType}")
    // private String fileStorageType;
    //
    // @Value("${fx.fileStorageRootPath}")
    // private String fileStorageRootPath;

    @Value("user1001")
    private String user;


    public FileController() {

    }
    public COSClient getCosClient(){
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(bucketRegion));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        return cosclient;
    }



    @PostMapping("/checkfiles")
    public Msg checkFiles(){


        return Msg.success("ok").put("state", "成功");

    }
    /**
     * 上传文件 到腾讯云服务器（https://cloud.tencent.com/document/product/436/10199）
     *
     * @return
     */
    @PostMapping(value = "/uploadtocos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Object Upload(@RequestParam(value = "file") MultipartFile file, HttpSession session) {
        if (file == null) {
            return new fileUpload.UploadMsg(0, "文件为空", null);
        }
        String oldFileName = file.getOriginalFilename();
        double fileSize = file.getSize();
        String eName = oldFileName.substring(oldFileName.lastIndexOf("."));
        // MultipartFile  file;
        byte[] byteArr = new byte[0];
        try {
            byteArr = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = new ByteArrayInputStream(byteArr);
        String fileHshCode = Md5Utils.md5HashCode32(inputStream);
        String newFileName = fileHshCode + eName;
        //UUID.randomUUID()+eName;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);

        // 生成cos客户端
        COSClient cosclient = CosClient.getInstance();
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String bucketName = this.bucketName;

        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口
        // 大文件上传请参照 API 文档高级 API 上传
        File localFile = null;
        try {
            localFile = File.createTempFile("temp", null);
            file.transferTo(localFile);
            // 指定要上传到 COS 上的路径
            String key = "/" + year + "/" + (month + 1) + "/" + day + "/" + newFileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            log.info("FileName:" + oldFileName + "  fileSize:" + fileSize + "  fileHashCode:" + fileHshCode);
            // return new UploadMsg(0, "上传成功", this.path + putObjectRequest.getKey());
           return Msg.success("上传成功").put("list", this.path + putObjectRequest.getKey());
        } catch (IOException e) {
            return Msg.error(500,e.getMessage());
                    // new UploadMsg(500, e.getMessage(), null);
        } finally {
            // 关闭客户端(关闭后台线程)
            // cosclient.shutdown();
        }
    }


    @PostMapping("createfolder")
    public Msg uploadfile(){


        return null;
    }


    /**
     * 创建文件夹
     *
     * @param filename
     * @return
     */
    @PostMapping("/createfoldercos")
    @ResponseBody
    public Msg createFolder(String filename) {
        // String folderName=jsonObject.get("name").toString()+"/";
        // String bucketName = "examplebucket-1250000000";
        String key = "/user1001/" + filename + "/";
        log.info(key);
        // 目录对象即是一个/结尾的空文件，上传一个长度为 0 的 byte 流
        InputStream input = new ByteArrayInputStream(new byte[0]);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0);
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(bucketRegion));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, key, input, objectMetadata);
        PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
        return Msg.success("创建成功").put("path", this.path + putObjectRequest.getKey());
    }


    /**
     * 删除文件
     *
     * @param filename
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public Msg deleteFile(String filename) {
        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        // String bucketName = "examplebucket-1250000000";
        // 指定被删除的文件在 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示删除位于 folder 路径下的文件 picture.jpg
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(bucketRegion));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);

        String key = "/user1001/" + filename + "/";
        cosclient.deleteObject(bucketName, key);
        return Msg.success("删除成功").put("file",this.path + "/" + filename + "");
    }


    @PostMapping("rename")
    public Msg rename(@RequestBody FileBean file){

        return Msg.success("重命名成功").put("name", "1");
    }


    /**
     * 获取文件列表
     *
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Msg list(String folder) {
        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        // String bucketName = "examplebucket-1250000000";

        // 生成cos客户端
        COSClient cosclient = CosClient.getInstance();

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 设置bucket名称
        listObjectsRequest.setBucketName(bucketName);
        // prefix表示列出的object的key以prefix开始
        listObjectsRequest.setPrefix(folder + "/");
        // deliter表示分隔符, 设置为/表示列出当前目录下的object, 设置为空表示列出所有的object
        listObjectsRequest.setDelimiter("/");
        // 设置最大遍历出多少个对象, 一次listobject最大支持1000
        listObjectsRequest.setMaxKeys(1000);
        ObjectListing objectListing = null;
        do {
            try {
                objectListing = cosclient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
                return null;
            } catch (CosClientException e) {
                e.printStackTrace();
                return null;
            }
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
            List<String> commonPrefixs = objectListing.getCommonPrefixes();

            // object summary表示所有列出的object列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 文件的路径key
                String key = cosObjectSummary.getKey();
                // 文件的etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                // 文件的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
            }

            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
        return Msg.success("ok").put("list", objectListing);
    }

    /**
     * 搜索文件
     * @return
     */
    @PostMapping("search")
    @ResponseBody
    public Msg search(String keywords){

        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        // String bucketName = "examplebucket-1250000000";
        log.info("keywords:"+keywords);
        String key = keywords;
        ObjectMetadata objectMetadata1 = getCosClient().getObjectMetadata(bucketName, key);

        // 获得本次请求的 requestId
        System.out.println(objectMetadata1.getRequestId());
        // 获得对象的 CRC64 校验值
        System.out.println(objectMetadata1.getCrc64Ecma());
        // 获得对象最近一次上传时间
        System.out.println(objectMetadata1.getLastModified());
        // 获得对象大小
        System.out.println(objectMetadata1.getContentLength());
        // 获得对象存储类型
        System.out.println(objectMetadata1.getStorageClass());
        return Msg.success("ok").put("list", objectMetadata1);
    }


    /**
     * 文件下载(通过自己服务器下载)
     * @param dlfilename
     * @param response
     */
    @GetMapping("/dl")
    public void download(@RequestParam("filename") String dlfilename,HttpServletResponse response) {
        // 生成cos客户端
        COSClient cosclient = CosClient.getInstance();
        // http://localhost:8080/file/dl?filename=user1001/ssm%E6%A1%86%E6%9E%B6%E6%90%AD%E5%BB%BA.md
        try {
            String key = dlfilename;
            String[] fn = key.split("/");
            String outputFilePath = fn[fn.length-1];
            log.info("outputFilePath:"+outputFilePath);
            File file = new File(outputFilePath);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ObjectMetadata downObjectMeta = cosclient.getObject(getObjectRequest, file);
            // path是指想要下载的文件的路径
            // File file = new File(path);
            // log.info(file.getPath());
                // 获取文件名
             String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            log.info("文件后缀名：" + ext);

            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }


}