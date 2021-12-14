package com.fx.pan.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

/**
 * @Author leaving
 * @Date 2021/11/25 10:58
 * @Version 1.0
 */

public class FileUtils {

    /**
     * 离线下载
     * @param response
     * @throws MalformedURLException
     */
    public void downloadNet(HttpServletResponse response) throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = new URL("windine.blogdriver.com/logo.gif");

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream("c:/abc.gif");

            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readImageFile(String path) {
        String[] pathArr = path.split("\\.");
        String imgType = pathArr[pathArr.length - 1];
        File f = new File(path);
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //经测试转换的图片是格式这里就什么格式，否则会失真
            ImageIO.write(bi, imgType, baos);
            byte[] bytes = baos.toByteArray();

            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String multipartFileToFile(MultipartFile file){
        String fullPath = null;
        if (file != null) {
            try {
                String fileRealName = file.getOriginalFilename();//获得原始文件名;
                int pointIndex =  fileRealName.lastIndexOf(".");//点号的位置
                String fileSuffix = fileRealName.substring(pointIndex);//截取文件后缀
                String fileNewName = UUID.randomUUID().toString();//新文件名,时间戳形式yyyyMMddHHmmssSSS
                String saveFileName = fileNewName.concat(fileSuffix);//新文件完整名（含后缀）
                String filePath  = "src\\main\\resources\\tmp\\"+saveFileName;
                File path = new File(filePath); //判断文件路径下的文件夹是否存在，不存在则创建
                // if (!path.exists()) {
                //     path.mkdirs();
                // }
                File savedFile = new File(filePath);
                boolean isCreateSuccess = savedFile.createNewFile(); // 是否创建文件成功
                if(isCreateSuccess){      //将文件写入
                    //第一种
                    file.transferTo(savedFile);
                    //第二种
                    savedFile = new File(filePath,saveFileName);
                    // 使用下面的jar包
                    copyInputStreamToFile(file.getInputStream(),savedFile);
                    fullPath= savedFile.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("文件是空的");
        }
        return fullPath;
    }




}
