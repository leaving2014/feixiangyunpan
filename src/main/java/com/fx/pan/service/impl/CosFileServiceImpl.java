package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fx.pan.config.cos.CosProperties;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.factory.FxUtils;
import com.fx.pan.service.CosFileService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/5/8 13:52
 * @version 1.0
 */
@Service("cosFileService")
public class CosFileServiceImpl implements CosFileService {

    @Autowired
    private COSClient cosClient;
    @Autowired
    private CosProperties cosProperties;

    @Value("${tencent.cos.accessKey}")
    private String accessKey;
    @Value("${tencent.cos.secretKey}")
    private String secretKey;
    @Value("${tencent.cos.bucketRegion}")
    private String bucketRegion;
    @Value("${tencent.cos.bucketName}")
    private String bucketName;
    @Value("${tencent.cos.path}")
    private String path;


    public boolean upload(MultipartFile file, FileBean fileBean) {
        // if (file == null) {
        //     throw new RuntimeException("文件不能为空");
        // }
        String newFileName = fileBean.getFileName();
        String dataStr = fileBean.getFileUrl().substring(0, 8);

        String bucketName = this.bucketName;
        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口
        // 大文件上传请参照 API 文档高级 API 上传
        String filePath = FxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
        File localFile = new File(filePath);
        try {
            // 指定要上传到 COS 上的路径
            String key = "file" + "/" + dataStr + "/" + newFileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            System.out.println("上传文件md5====" + putObjectResult.getETag());
            return true;
        } finally {
            // 关闭客户端(关闭后台线程)
            // cosclient.shutdown();
        }
    }


    @Override
    public boolean upload(MultipartFile file) {
        return false;
    }

    @Async
    @Override
    public ImageAuditingResponse fileAudit(FileBean fileBean, Boolean skipUpload) {
        if (!skipUpload) {
            upload(null, fileBean);
        }
        // boolean upload = upload(null, fileBean);

        //1.创建任务请求对象
        ImageAuditingRequest request = new ImageAuditingRequest();
        //2.添加请求参数 参数详情请见api接口文档
        //2.1设置请求bucket
        request.setBucketName(bucketName);
        //2.2设置审核类型
        request.setDetectType("porn,terrorist");
        String dataStr = fileBean.getFileUrl().substring(0, 8);
        //2.3设置bucket中的图片位置
        String filepath = "file/" + dataStr + "/" + fileBean.getFileName();
        System.out.println("文件审核文件位置======" + filepath);
        request.setObjectKey(filepath);
        //3.调用接口,获取任务响应对象
        ImageAuditingResponse response = cosClient.imageAuditing(request);
        String s = JSONObject.toJSONString(response);
        System.out.println("文件审核返回结果====" + s);
        return response;
        // {jobId='si07bda9bbceca11eca0e55254009a49da', object='null', compressionResult='0', result='0',
        // label='Normal', category='', subLabel='', score='0', text='全部 冻结状态: 审核状态: 全部 30天
        // 2022-05-0800:00:00至2022-05-0823:5959  图片分值: 至 100', creationTime='null', code='null', message='null',
        // state='null', dataId='null', url='null', pornInfo=AudtingCommonInfo{code='0', msg='OK', hitFlag='0',
        // score='0', label='', keywords='null', count='null', subLabel='null', ocrResults=null, category='null',
        // objectResults=[]}, terroristInfo=AudtingCommonInfo{code='0', msg='OK', hitFlag='0', score='0', label='',
        // keywords='null', count='null', subLabel='null', ocrResults=null, category='null', objectResults=[]},
        // politicsInfo=null, adsInfo=null, teenagerInfo=AudtingCommonInfo{code='null', msg='null', hitFlag='null',
        // score='null', label='null', keywords='null', count='null', subLabel='null', ocrResults=null,
        // category='null', objectResults=[]}, userInfo=UserInfo{tokenId='null', nickname='null', deviceId='null',
        // appId='null', room='null', ip='null', type='null'}}
    }

    @Override
    public ResponseResult createFolder(String filename) {
        // String folderName=jsonObject.get("name").toString()+"/";
        // String bucketName = "examplebucket-1250000000";
        String key = "/file/" + filename + "/";
        // log.info(key);
        // 目录对象即是一个/结尾的空文件，上传一个长度为 0 的 byte 流
        InputStream input = new ByteArrayInputStream(new byte[0]);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0);
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(bucketRegion));
        // 3 生成cos客户端
        // COSClient cosclient = CosClient.getInstance();
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, key, input, objectMetadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        // ResponseResult responseResult = new ResponseResult().ok
        Map map = new HashMap();
        map.put("path", this.path + putObjectRequest.getKey());
        return ResponseResult.success("创建成功", map);
    }
}
