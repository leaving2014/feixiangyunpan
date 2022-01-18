package com.fx.pan.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
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


    /**
     *离线下载获取文件名称
     * @param link 下载文件url
     * @return fileName 下载文件名称
     * @throws IOException
     */
    public static String getFileName(String link) throws IOException {
        String imgurl = link;
        URL url = new URL(imgurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        //获取文件名和扩展名
        // 先连接一次，解决跳转下载
        conn.getResponseCode();
        imgurl = conn.getURL().toString();
        //第一种方式，针对 img.png
        String fileName = imgurl.substring(imgurl.lastIndexOf("/") + 1);
        String extName = null;
        if (fileName.lastIndexOf(".") > 0) {
            extName = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        if (extName == null || extName.length() > 4 || extName.indexOf("?") > -1) {
            //第二种方式，获取header 确定文件名和扩展名
            fileName = conn.getHeaderField("Content-Disposition");
            if (fileName == null || fileName.indexOf("file") < 0) {
                int index = link.indexOf("?");
                if(index != -1) {
                    fileName = link.substring(0, index);
                    String[] fileNameArr = fileName.split("/");
                    fileName= fileNameArr[fileNameArr.length-1];
                }
            } else {
                if (fileName.indexOf("filename")!=-1){
                    fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename") + 9,
                            fileName.length() - 1), "UTF-8");
                    System.out.println("filename:"+fileName);
                }else if (fileName.indexOf("fileName")!=-1)  {
                    fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("fileName") + 9,
                            fileName.length() - 1), "UTF-8");
                    System.out.println("fileName:"+fileName);

                }
                extName = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
        }
        return fileName;
    }


    /**
     *
     * @param rawSize
     * @return
     */
    public double fileSizeUnitConversion(long rawSize){
        //26.71KB    fileSize:27352.0
        double conversionSize=0;
        if (rawSize<1024){
            // B
            conversionSize= rawSize;
        }else if (rawSize<1024*1024){
            //KB
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize / 1024));
        }else if(rawSize<Math.pow(1024,3)){
            // MB
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize / Math.pow(1024,2)));
        }else if (rawSize<Math.pow(1024,4)){
            //GB
            conversionSize= Double.parseDouble(String.format("%.2f",rawSize / Math.pow(1024,3)));
        }
        return conversionSize;
    }

    /**
     *
     * @param rawSize
     * @return
     */
    public static String fileSizeUnitConversionAndUnit(long rawSize){
        //26.71KB    fileSize:27352.0
        double conversionSize=0;
        String unit="";
        if (rawSize<1024){
            unit="B";
            conversionSize= rawSize;
        }else if (rawSize<1024*1024){
            unit="KB";
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize*1.00 / 1024));
        }else if(rawSize<Math.pow(1024,3)){
            unit="MB";
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize*1.00/ Math.pow(1024,2)));
        }else if (rawSize<Math.pow(1024,4)){
            unit="GB";
            conversionSize= Double.parseDouble(String.format("%.2f",rawSize*1.00 / Math.pow(1024,3)));
        }
        return conversionSize+" "+unit;
    }

}
