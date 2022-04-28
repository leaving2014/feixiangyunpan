package com.fx.pan.utils;

import cn.hutool.core.date.DateUtil;
import com.fx.pan.common.Constants;
import com.fx.pan.domain.Chunk;
import com.fx.pan.domain.FileBean;
import com.fx.pan.service.FileService;
import com.fx.pan.service.StorageService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.file.FileTypeUtils;
import com.fx.pan.utils.file.ImageUtil;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

/**
 * @Author leaving
 * @Date 2021/11/25 10:58
 * @Version 1.0
 */
@Component
public class FileUtils {

    static int bufferSize = 8192;//单位bytes
    private static String absoluteFilePath;
    private static String absoluteCachePath;

    @Value("${fx.absoluteFilePath}")
    public void setAbsoluteFilePath(String absoluteFilePath) {
        FileUtils.absoluteFilePath = absoluteFilePath;
    }

    @Value("${fx.absoluteCachePath}")
    public void setAbsoluteCachePath(String absoluteCachePath) {
        FileUtils.absoluteCachePath = absoluteCachePath;
    }

    @Autowired
    private FileService fs;

    private static FileService fileService;

    public @PostConstruct
    void init() {
        fileService = fs;
    }

    @Autowired
    private StorageService storage;

    private static StorageService storageService;

    public @PostConstruct
    void init2() {
        storageService = storage;
    }

    /**
     * 离线下载
     *
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

    public static String multipartFileToFile(MultipartFile file) {
        String fullPath = null;
        if (file != null) {
            try {
                String fileRealName = file.getOriginalFilename();//获得原始文件名;
                int pointIndex = fileRealName.lastIndexOf(".");//点号的位置
                String fileSuffix = fileRealName.substring(pointIndex);//截取文件后缀
                String fileNewName = UUID.randomUUID().toString();//新文件名,时间戳形式yyyyMMddHHmmssSSS
                String saveFileName = fileNewName.concat(fileSuffix);//新文件完整名（含后缀）
                String filePath = "src\\main\\resources\\tmp\\" + saveFileName;
                File path = new File(filePath); //判断文件路径下的文件夹是否存在，不存在则创建
                // if (!path.exists()) {
                //     path.mkdirs();
                // }
                File savedFile = new File(filePath);
                boolean isCreateSuccess = savedFile.createNewFile(); // 是否创建文件成功
                if (isCreateSuccess) {      //将文件写入
                    //第一种
                    file.transferTo(savedFile);
                    //第二种
                    savedFile = new File(filePath, saveFileName);
                    // 使用下面的jar包
                    copyInputStreamToFile(file.getInputStream(), savedFile);
                    fullPath = savedFile.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件是空的");
        }
        return fullPath;
    }


    /**
     * 离线下载获取文件名称
     *
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
                if (index != -1) {
                    fileName = link.substring(0, index);
                    String[] fileNameArr = fileName.split("/");
                    fileName = fileNameArr[fileNameArr.length - 1];
                }
            } else {
                if (fileName.indexOf("filename") != -1) {
                    fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename") + 9,
                            fileName.length() - 1), "UTF-8");
                    System.out.println("filename:" + fileName);
                } else if (fileName.indexOf("fileName") != -1) {
                    fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("fileName") + 9,
                            fileName.length() - 1), "UTF-8");
                    System.out.println("fileName:" + fileName);

                }
                extName = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
        }
        return fileName;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String getFileExtendName(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }


    /**
     * @param rawSize
     * @return
     */
    public static double fileSizeUnitConversion(Long rawSize) {
        //26.71KB    fileSize:27352.0
        double conversionSize = 0;
        if (rawSize < 1024) {
            // B
            conversionSize = rawSize;
        } else if (rawSize < 1024 * 1024) {
            //KB
            conversionSize = Double.parseDouble(String.valueOf(rawSize / Math.pow(1024, 1)));
        } else if (rawSize < Math.pow(1024, 3)) {
            // MB
            conversionSize = Double.parseDouble(String.format("%.2f", rawSize / Math.pow(1024, 2)));
        } else if (rawSize < Math.pow(1024, 4)) {
            //GB
            conversionSize = Double.parseDouble(String.format("%.2f", rawSize / Math.pow(1024, 3)));
        }
        return conversionSize;
    }

    /**
     * @param rawSize
     * @return
     */
    public static String fileSizeUnitConversionAndUnit(long rawSize) {
        //26.71KB    fileSize:27352.0
        double conversionSize = 0;
        String unit = "";
        if (rawSize < 1024) {
            unit = "B";
            conversionSize = rawSize;
        } else if (rawSize < 1024 * 1024) {
            unit = "KB";
            conversionSize = Double.parseDouble(String.format("%.2f", rawSize * 1.00 / 1024));
        } else if (rawSize < Math.pow(1024, 3)) {
            unit = "MB";
            conversionSize = Double.parseDouble(String.format("%.2f", rawSize * 1.00 / Math.pow(1024, 2)));
        } else if (rawSize < Math.pow(1024, 4)) {
            unit = "GB";
            conversionSize = Double.parseDouble(String.format("%.2f", rawSize * 1.00 / Math.pow(1024, 3)));
        }
        return conversionSize + " " + unit;
    }


    public static String getCacheFileFullPath(String path, String date, String fileName) {
        String newPath = path + "/" + date + "/" + fileName;
        System.out.println("生成文件全路径:" + newPath);
        return newPath;
    }

    /**
     * 获取文件扩展名
     *
     * @return
     */
    public static String getFileExt(String filename) {
        int index = filename.lastIndexOf(".");

        if (index == -1) {
            return null;
        }
        String result = filename.substring(index + 1);
        return result;
    }

    /**
     * 获取本地文件存储的路径 创建文件的全路径
     *
     * @param basePath
     * @param date
     * @param fileName
     * @return
     */
    public static String getLocalStorageFilePath(String basePath, String date, String fileName) {
        return basePath + "/" + date + "/" + fileName;
    }

    /**
     * 获取文件的存储完整路径
     *
     * @param fileBean
     * @return
     */
    public static String getLocalStorageFilePathByFileBean(FileBean fileBean) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(fileBean.getFileCreateTime());
        return absoluteFilePath + "/" + date + "/" + fileBean.getIdentifier() + "." + fileBean.getFileExt();
    }

    /**
     * 获取文件缓存完整路径
     *
     * @param fileBean
     * @return
     */
    public static String getLocalStorageFileCachePathByFileBean(FileBean fileBean) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String date = sf.format(fileBean.getFileCreateTime());
        return absoluteCachePath + "/" + date + "/" + fileBean.getIdentifier() + "." + fileBean.getFileExt();
    }

    /**
     * 获取文件全名(包含扩展名)
     *
     * @param fileBean
     * @return
     */
    public static String getFileFullName(FileBean fileBean) {
        if (fileBean.getIsDir() == 1) {
            throw new RuntimeException("非文件类型");
        }
        return fileBean.getFileName() + "." + fileBean.getFileExt();
    }

    /**
     * 获得拷贝对象
     *
     * @param copyFileBean
     * @return
     */
    public static FileBean getCopyObject(FileBean copyFileBean, FileBean targetFileBean) {
        FileBean newFile = null;
        try {
            newFile = (FileBean) copyFileBean.clone();
            newFile.setId(null);
            newFile.setFilePath(targetFileBean.getFilePath() + targetFileBean.getFileName());
            newFile.setFileUpdateTime(null);
            newFile.setFileCreateTime(null);
            newFile.setOrigin(1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * 返回上传文件对象
     *
     * @param chunk
     * @param userId
     * @return
     */
    public static FileBean getUploadFileBean(Chunk chunk, Date date, Integer storageType, Long userId) throws IOException {
        FileBean f = new FileBean();
        String fileName = chunk.getFilename();
        f.setFileName(fileName);
        f.setFilePath(chunk.getFilePath());
        f.setIsDir(0);
        f.setFileSize(chunk.getTotalSize());

        String extendName = FileUtils.getFileExt(fileName);
        f.setFileExt(extendName);

        f.setIdentifier(chunk.getIdentifier());
        f.setStorageType(storageType);
        Integer fileType = FileTypeUtils.getFileTypeByExtendName(FileUtils.getFileExt(fileName));
        System.out.println("文件类型:" + fileType);

        f.setFileCreateTime(date);
        f.setFileUpdateTime(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMdd");
        String formatDate = simpleDateFormat.format(date);

        f.setFileUrl(formatDate + "/" + chunk.getIdentifier() + "." + extendName);

        // 生成缩略图     || fileType == 2
        if (fileType == 1) {
            ImageUtil.startGenerateThumbnail(absoluteFilePath + "/" + formatDate + "/" + chunk.getIdentifier() + "." + extendName, f, true, 0.3);
        }

        f.setFileType(fileType);

        f.setUserId(userId);
        System.out.println("文件信息:" + f.toString());
        return f;
    }

    // 返回文件转换FileBean对象
    public static FileBean getFileBeanByPath(String filePath, String path, Date date, Integer storageType,
                                             Long userId) throws FileNotFoundException {
        FileBean f = new FileBean();
        File file = new File(filePath);
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        f.setFileName(fileName);
        if (path != null) {
            f.setFilePath(path);
        } else {
            f.setFilePath("/");
        }
        f.setIsDir(0);
        f.setFileSize(file.length());

        String extendName = FileUtils.getFileExt(fileName);
        f.setFileExt(extendName);
        f.setIdentifier(Md5Utils.md5HashCode32(filePath));
        f.setStorageType(storageType);
        Integer fileType = FileTypeUtils.getFileTypeByExtendName(FileUtils.getFileExt(fileName));
        f.setFileCreateTime(date);
        f.setFileUpdateTime(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMdd");
        String formatDate = simpleDateFormat.format(date);
        f.setFileUrl(formatDate + "/" + f.getIdentifier() + "." + extendName);
        f.setFileType(fileType);
        f.setUserId(userId);
        return f;
    }

    // 移动文件然后重命名
    public static void moveFile(String oldFilePath, String newFilePath, boolean rename, String newName) throws IOException {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
        if (rename) {
            oldFile.renameTo(newFile);
        }
    }

    // 重命名文件
    public static void renameFile(String oldFilePath, String newFilePath) throws IOException {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
    }

    /**
     * 创建下载文件压缩包
     */
    public static File createDownloadZipFile(String path) {
        File file = new File(path);
        return file;
    }

    /**
     * 压缩文件
     *
     * @param srcFiles
     * @param destFile
     * @throws IOException
     */
    public static boolean doCompressFiles(List<File> srcFiles, File destFile) throws IOException {
        ZipArchiveOutputStream out = null;
        InputStream is = null;
        try {

            out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(destFile), bufferSize));

            for (File srcFile : srcFiles) {
                is = new BufferedInputStream(new FileInputStream(srcFile), bufferSize);
                ZipArchiveEntry entry = new ZipArchiveEntry(srcFile.getName());
                entry.setSize(srcFile.length());
                out.putArchiveEntry(entry);
                IOUtils.copy(is, out);
            }
            out.closeArchiveEntry();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);

        }
        return true;
    }

    public static FileBean getUnzipFileBean(String filePath, String path) {
        Long userId = SecurityUtils.getUserId();
        FileBean fileBean = new FileBean();
        fileBean.setFileCreateTime(new Date());
        fileBean.setFileUpdateTime(new Date());
        File file = new File(filePath);
        // FileBean parentFileBean = fileService.selectByFilePath(path,userId);
        // 判断文件是否是目录
        if (file.isDirectory()) {
            fileBean.setIsDir(1);
            fileBean.setFileName(file.getName());
            fileBean.setFilePath(path);
            fileBean.setUserId(userId);
        } else {
            fileBean.setIsDir(0);
            fileBean.setFileName(file.getName());
            fileBean.setFilePath(path);
            fileBean.setUserId(userId);
            fileBean.setFileSize(file.length());
            fileBean.setFileType(FileTypeUtils.getFileTypeByExtendName(FileUtils.getFileExt(file.getName())));
            fileBean.setFilePath(path);
            fileBean.setFileExt(FileUtils.getFileExt(file.getName()));
            // fileBean.setParentPathId(parentFileBean.getId());

        }
        return fileBean;
    }

    /**
     * @param zipFilePath  待解压文件
     * @param desDirectory 解压到的目录
     * @param zipFileBean  解压文件对象
     * @param path         数据库存储目录
     * @return
     * @throws Exception
     */
    public static boolean unzipFile(String zipFilePath, String desDirectory, FileBean zipFileBean, String path,
                                    PropertyChangeListener propertyChangeListener) throws Exception {
        Date date = new Date();
        String dateStr = DateUtil.format(date, "yyyyMMdd");
        File desDir = new File(desDirectory);
        long totalFileSize = new File(zipFilePath).length();// 总大小
        long readSize = 0;
        Long totalSize = 0L;
        if (!desDir.exists()) {
            boolean mkdirSuccess = desDir.mkdir();
            if (!mkdirSuccess) {
                throw new Exception("创建解压目标文件夹失败");
            }
        }
        // 读入流
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath),
                Charset.forName(Constants.GBK));
        // 遍历每一个文件
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        System.out.println("zipEntry:" + zipEntry);
        int i = 0;
        while (zipEntry != null) {
            i++;
            String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
            FileBean fileBean = BeanCopyUtils.copyBean(getUnzipFileBean(unzipFilePath, path), FileBean.class);

            if (zipEntry.isDirectory()) { // 文件夹
                // 直接创建
                // mkdir(new File(unzipFilePath));
                System.out.println("文件夹FileBean:" + fileBean);
                FileBean parentFileBean = fileService.selectByFilePath(path, zipFileBean.getUserId());
                fileBean.setParentPathId(parentFileBean.getId());
                fileBean.setFileType(null);
                fileBean.setFileExt(null);
                System.out.println("文件夹FileBean:" + i + " " + fileBean);
                // 当压缩包内有一个和压缩包名相同的文件夹时，会出现重复文件夹的情况
                if (!zipEntry.getName().equals(zipFileBean.getFileName())) {
                    fileService.save(fileBean);
                }
            } else { // 文件
                // String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
                File file = new File(unzipFilePath);
                totalSize += file.length();

                // 创建父目录
                System.out.println("创建父目录:" + file.getParentFile());
                mkdir(file.getParentFile());
                // 写出文件流
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(new FileOutputStream(unzipFilePath));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, readLen);
                }
                bufferedOutputStream.close();

                // 写出文件后在对文件进行md5校验
                String md5 = Md5Utils.md5HashCode32(unzipFilePath);
                fileBean.setIdentifier(md5);
                fileBean.setFileSize(file.length());
                fileBean.setFileUrl(dateStr + "/" + md5 + "." + FileUtils.getFileExt(file.getName()));
                System.out.println("文件FileBean:" + fileBean);
                FileBean parentFileBean = fileService.selectByFilePath(path, zipFileBean.getUserId());
                // fileService.selectParentPathById();
                //
                fileBean.setParentPathId(parentFileBean.getId());
                fileService.save(fileBean);
                System.out.println("文件FileBean:" + i + " " + fileBean);

                // 对文件重命名
                String newFilePath = desDirectory + "/" + md5 + "." + FileUtils.getFileExt(zipEntry.getName());
                file.renameTo(new File(newFilePath));
                // 删除临时文件
                file.delete();
            }
            Integer oldValue = (int) ((readSize * 1.0 / totalFileSize) * 100);// 已解压的字节大小占总字节的大小的百分比
            readSize += zipEntry.getCompressedSize();// 累加字节长度
            Integer newValue = (int) ((readSize * 1.0 / totalFileSize) * 100);// 已解压的字节大小占总字节的大小的百分比
            if (propertyChangeListener != null) {// 通知调用者解压进度发生改变
                propertyChangeListener.propertyChange(new PropertyChangeEvent(zipFilePath, "progress", oldValue,
                        newValue));
            }

            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        storageService.updateStorageUse(totalSize, zipFileBean.getUserId());
        zipInputStream.close();
        return true;
    }

    // 如果父目录不存在则创建
    private static void mkdir(File file) {
        if (null == file || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        file.mkdir();
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        String zipFilePath = "D:\\开发\\test\\test1.zip";
        String desDirectory = "D:\\ideaWorkspace\\pan\\src\\main\\resources\\static\\file";
        // unzip(zipFilePath, desDirectory);
    }

}
