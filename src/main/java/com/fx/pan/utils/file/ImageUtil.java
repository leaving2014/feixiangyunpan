package com.fx.pan.utils.file;


import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import com.fx.pan.domain.FileBean;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.utils.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
// import org.bytedeco.javacv.FFmpegFrameGrabber;
// import org.bytedeco.javacv.Frame;
// import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 图片压缩工具类
 *
 * @author lnj
 * createTime 2018-10-19 15:31
 **/
@Slf4j
@PropertySource(value = {"classpath:application.yml"},ignoreResourceNotFound = true,encoding="UTF-8")
@Component
public class ImageUtil {
    private static String absoluteCachePath;
    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Value("${fx.absoluteCachePath}")
    public void setAbsoluteCachePath(String absoluteCachePath) {
        ImageUtil.absoluteCachePath = absoluteCachePath;
    }

    private static final String IMAGEMAT = "png";
    private static final String ROTATE = "rotate";
    public static final String[] IMG_FILE = new String[]{"bmp", "jpg", "png", "tif", "gif", "jpeg"};
    /**
     * 默认截取视频的中间帧为封面
     */
    public static final int MOD = 2;


    // 图片默认缩放比率
    private static final double DEFAULT_SCALE = 0.3d;

    // 缩略图后缀
    private static final String SUFFIX = "-thumbnail";

    // @Async
    public static void startGenerateThumbnail(String filePath, FileBean fileBean, boolean scale, double scaleSize) throws IOException {
        if (Arrays.asList(IMG_FILE).contains(fileBean.getFileExt())) {
            ImageUtil.generateThumbnail(filePath, fileBean, true, 0.3);
        } else if (fileBean.getFileType() == 2) {
            // 视频生成缩略图 由于视频生成缩略图依赖过多，暂时不使用
        }
    }

    @SneakyThrows
    public static void generateThumbnail(String filePath, FileBean fileBean, boolean scale, double scaleSize) {
        System.out.println("要生成缩略图移动文件路径====" + filePath);
        File file = new File(filePath);
        String fileName = file.getName();
        Date date = fileBean.getFileCreateTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMdd");
        String formatDate = simpleDateFormat.format(date);

        // 判断文件夹是否存再,不存在则创建
        System.out.println("IMageUtil下获取absoluteCachePath:::====="+absoluteCachePath);
        File folder = new File(absoluteCachePath + "/" + formatDate);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }

        /**
         * 指定大小进行缩放
         * 若图片横比200小，高比300小，不变
         * 若图片横比200小，高比300大，高缩小到300，图片比例不变
         * 若图片横比200大，高比300小，横缩小到200，图片比例不变
         * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300
         */
        // Thumbnails.of(filePath)
        //         .size(200, 300)
        //         .toFile(file.getAbsolutePath() + "_200x300.jpg");


        /**
         * 按照比例进行缩放
         * scale(比例)
         * */
        Long originFileSize = fileBean.getFileSize();
        // 小于500KB不生成缩略图
        if (fileBean.getFileSize() < 512000L) {
            return;
        }
        float scaleRatio;

        float fileSizeMib = (float) FileUtils.fileSizeUnitConversion(originFileSize);

        if (originFileSize > 1048576) { // 图片大于1MB
            scaleRatio = 0.5f;

            if (fileSizeMib > 20) { // 图片大于20MB
                scaleRatio = 0.01f;
            } else if (fileSizeMib >= 10 && fileSizeMib < 20) {  // 图片大于10MB小于20MB
                scaleRatio = scaleRatio - fileSizeMib * 0.025f;
            } else { // 图片小于10MB
                scaleRatio = 0.2f - fileSizeMib * 0.01f;
            }

        } else {
            scaleRatio = 0.3f;
        }
        log.info("图片缩放比例为" + scaleRatio);

        String path = FileUtils.getCacheFileFullPath(absoluteCachePath, formatDate,
                fileName);
        System.out.println("按照比例进行缩放生成文件路径:====" + path);
        Thumbnails.of(filePath)
                .scale(scaleRatio)
                .toFile(path);


        /**
         *  不按照比例，指定大小进行缩放
         *  keepAspectRatio(false) 默认是按照比例缩放的
         * */
        // Thumbnails.of(filePath)
        //         .size(200, 200)
        //         .keepAspectRatio(false)
        //         .toFile(file.getAbsolutePath() + "_200x200.jpg");

        /**
         *  输出图片到流对象
         *
         * */
        // OutputStream os = new FileOutputStream(file.getAbsolutePath() + "_OutputStream.png");
        // Thumbnails.of(filePath)
        //         .size(1280, 1024)
        //         .toOutputStream(os);

        /**
         *  输出图片到BufferedImage
         * **/
        // BufferedImage thumbnail = Thumbnails.of(filePath)
        //         .size(1280, 1024)
        //         .asBufferedImage();
        // String path = FileUtil.getCacheFileFullPath(absoluteCachePath, formatDate,
        //         fileName);
        // System.out.println("缩略图路径");
        // ImageIO.write(thumbnail, fileBean.getFileExt(),
        //         new File(path));
    }

    /**
     * 生成缩略图到指定的目录
     *
     * @param path  目录
     * @param files 要生成缩略图的文件列表
     * @throws IOException
     */
    public static List<String> generateThumbnail2Directory(String path, String... files) throws IOException {
        return generateThumbnail2Directory(DEFAULT_SCALE, path, files);
    }

    /**
     * 生成缩略图到指定的目录
     *
     * @param scale    图片缩放率
     * @param pathname 缩略图保存目录
     * @param files    要生成缩略图的文件列表
     * @throws IOException
     */
    public static List<String> generateThumbnail2Directory(double scale, String pathname, String... files) throws IOException {
        Thumbnails.of(files)
                // 图片缩放率，不能和size()一起使用
                .scale(scale)
                // 缩略图保存目录,该目录需存在，否则报错
                .toFiles(new File(pathname), Rename.SUFFIX_HYPHEN_THUMBNAIL);
        List<String> list = new ArrayList<>(files.length);
        for (String file : files) {
            list.add(appendSuffix(file, SUFFIX));
        }
        return list;
    }

    /**
     * 将指定目录下所有图片生成缩略图
     *
     * @param pathname 文件目录
     */
    public static void generateDirectoryThumbnail(String pathname) throws IOException {
        generateDirectoryThumbnail(pathname, DEFAULT_SCALE);
    }

    /**
     * 将指定目录下所有图片生成缩略图
     *
     * @param pathname 文件目录
     */
    public static void generateDirectoryThumbnail(String pathname, double scale) throws IOException {
        File[] files = new File(pathname).listFiles();
        compressRecurse(files, pathname);
    }

    /**
     * 文件追加后缀
     *
     * @param fileName 原文件名
     * @param suffix   文件后缀
     * @return
     */
    public static String appendSuffix(String fileName, String suffix) {
        String newFileName = "";

        int indexOfDot = fileName.lastIndexOf('.');

        if (indexOfDot != -1) {
            newFileName = fileName.substring(0, indexOfDot);
            newFileName += suffix;
            newFileName += fileName.substring(indexOfDot);
        } else {
            newFileName = fileName + suffix;
        }

        return newFileName;
    }


    private static void compressRecurse(File[] files, String pathname) throws IOException {
        for (File file : files) {
            // 目录
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                compressRecurse(subFiles, pathname + File.separator + file.getName());
            } else {
                // 文件包含压缩文件后缀或非图片格式，则不再压缩
                String extension = getFileExtention(file.getName());
                if (!file.getName().contains(SUFFIX) && isImage(extension)) {
                    generateThumbnail2Directory(pathname, file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 根据文件扩展名判断文件是否图片格式
     *
     * @param extension 文件扩展名
     * @return
     */
    public static boolean isImage(String extension) {
        String[] imageExtension = new String[]{"jpeg", "jpg", "gif", "bmp", "png"};

        for (String e : imageExtension) {
            if (extension.toLowerCase().equals(e)) {
                return true;
            }
        }

        return false;
    }

    public static String getFileExtention(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension;
    }


    /**
     * 生成视频预览图 主方法
     *
     * @param filePath       视频文件本地路径
     * @param targerFilePath 目标文件夹
     * @param targetFileName 目标文件名
     * @return 图片文件路径
     * @throws Exception
     */
    // public static String randomGrabberFFmpegImage(String filePath, String targerFilePath, String targetFileName)
    //         throws Exception {
    //     System.out.println(filePath);
    //     FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(filePath);
    //     ff.start();
    //     Frame f;
    //     int lenght = ff.getLengthInFrames();
    //     int i = 0;
    //     String path = null;
    //     while (i < lenght) {
    //         // 过滤前5帧，避免出现全黑的图片，依自己情况而定
    //         f = ff.grabFrame();
    //         if ((i > 200) && (f.image != null)) {
    //             path = doExecuteFrame(f, targerFilePath, targetFileName);
    //             break;
    //         }
    //         i++;
    //     }
    //
    //     ff.stop();
    //     return path;
    // }

    /**
     * 执行帧处理
     *
     * @return
     * @throws Exception
     */
    // public static String doExecuteFrame(Frame f, String targerFilePath, String targetFileName) {
    //
    //     if (null == f || null == f.image) {
    //         // throw new GlobleException("获取缩略图失败");
    //     }
    //     Java2DFrameConverter converter = new Java2DFrameConverter();
    //     String imageMat = "jpg";
    //     String FileName = targerFilePath + File.separator + targetFileName + "." + imageMat;
    //     BufferedImage bi = converter.getBufferedImage(f);
    //     System.out.println("width:" + bi.getWidth());
    //     System.out.println("height:" + bi.getHeight());
    //     File output = new File(FileName);
    //     try {
    //         ImageIO.write(bi, imageMat, output);
    //     } catch (IOException e) {
    //         // throw new GlobleException("缩略图写入文件夹失败");
    //     }
    //     return FileName;
    // }
    public static void main(String[] args) throws Exception {
        // String s = randomGrabberFFmpegImage("/home/xiao/IMG_3077.mp4", "/home/xiao", "213");
        // String s = randomGrabberFFmpegImage("/home/xiao/IMG_3077.mp4", "/home/xiao", "213");
        // System.out.println(s);
    }
}
