package com.fx.pan.cos;

/**
 * @Author leaving
 * @Date 2021/11/24 21:47
 * @Version 1.0
 */

/**
 * 文件上传控制器
 */

public class fileUpload {
    public static class UploadMsg {
        public UploadMsg(int i, String 文件为空, Object o) {
        }
    }


    // @Value("${spring.tengxun.accessKey}")
    // private String accessKey;
    // @Value("${spring.tengxun.secretKey}")
    // private String secretKey;
    // @Value("${spring.tengxun.bucket}")
    // private String bucket;
    // @Value("${spring.tengxun.bucketName}")
    // private String bucketName;
    // @Value("${spring.tengxun.path}")
    // private String path;
    // @Value("${spring.tengxun.qianzui}")
    // private String qianzui;
    //
    // /**
    //  * 上传道腾讯云服务器（https://cloud.tencent.com/document/product/436/10199）
    //  * @return
    //  */
    // @RequestMapping(value = "/tengxun",method = RequestMethod.POST)
    // @ResponseBody
    // public Object Upload(@RequestParam(value = "file") MultipartFile file, HttpSession session){
    //     if(file == null){
    //         return new UploadMsg(0,"文件为空",null);
    //     }
    //     String oldFileName = file.getOriginalFilename();
    //     String eName = oldFileName.substring(oldFileName.lastIndexOf("."));
    //     String newFileName = UUID.randomUUID()+eName;
    //     Calendar cal = Calendar.getInstance();
    //     int year = cal.get(Calendar.YEAR);
    //     int month=cal.get(Calendar.MONTH);
    //     int day=cal.get(Calendar.DATE);
    //     // 1 初始化用户身份信息(secretId, secretKey)
    //     COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
    //     // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    //     ClientConfig clientConfig = new ClientConfig(new Region(bucket));
    //     // 3 生成cos客户端
    //     COSClient cosclient = new COSClient(cred, clientConfig);
    //     // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
    //     String bucketName = this.bucketName;
    //
    //     // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口
    //     // 大文件上传请参照 API 文档高级 API 上传
    //     File localFile = null;
    //     try {
    //         localFile = File.createTempFile("temp",null);
    //         file.transferTo(localFile);
    //         // 指定要上传到 COS 上的路径
    //         String key = "/"+this.qianzui+"/"+year+"/"+month+"/"+day+"/"+newFileName;
    //         PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
    //         PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
    //         return new UploadMsg(1,"上传成功",this.path + putObjectRequest.getKey());
    //     } catch (IOException e) {
    //         return new UploadMsg(-1,e.getMessage(),null);
    //     }finally {
    //         // 关闭客户端(关闭后台线程)
    //         cosclient.shutdown();
    //     }
    // }
    //
    // private class UploadMsg {
    //     public int status;
    //     public String msg;
    //     public String path;
    //
    //     public UploadMsg() {
    //         super();
    //     }
    //
    //     public UploadMsg(int status, String msg, String path) {
    //         this.status = status;
    //         this.msg = msg;
    //         this.path = path;
    //     }
    // }
}
