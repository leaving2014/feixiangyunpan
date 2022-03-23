package com.fx.pan.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author leaving
 * @Date 2022/3/3 9:04
 * @Version 1.0
 */

public class FileTypeUtils {

    public static Map<String, String> PATH_MAP = new HashMap();
    public static String LOCAL_STORAGE_PATH;
    public static final String[] IMG_FILE = new String[]{"bmp", "jpg", "png", "tif", "gif", "jpeg"};
    public static final String[] DOC_FILE = new String[]{"doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "hlp", "wps", "rtf", "html", "pdf"};
    public static final String[] VIDEO_FILE = new String[]{"avi", "mp4", "mpg", "mov", "swf"};
    public static final String[] MUSIC_FILE = new String[]{"wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"};
    public static final String[] TXT_FILE = new String[]{"txt", "html", "java", "xml", "js", "css", "json"};
    public static final int IMAGE_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int MUSIC_TYPE = 4;
    public static final int OTHER_TYPE = 5;
    public static final int SHARE_FILE = 6;
    public static final int RECYCLE_FILE = 7;

    public FileTypeUtils() {
    }

    public static List<String> getFileExtendsByType(int fileType) {
        Object fileExtends;
        switch(fileType) {
            case 1:
                fileExtends = Arrays.asList(IMG_FILE);
                break;
            case 2:
                fileExtends = Arrays.asList(DOC_FILE);
                break;
            case 3:
                fileExtends = Arrays.asList(VIDEO_FILE);
                break;
            case 4:
                fileExtends = Arrays.asList(MUSIC_FILE);
                break;
            default:
                fileExtends = new ArrayList();
        }

        return (List)fileExtends;
    }

    public static boolean isImageFile(String extendName) {
        String[] var1 = IMG_FILE;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String extend = var1[var3];
            if (extendName.equalsIgnoreCase(extend)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isVideoFile(String extendName) {
        String[] var1 = VIDEO_FILE;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String extend = var1[var3];
            if (extendName.equalsIgnoreCase(extend)) {
                return true;
            }
        }

        return false;
    }

    public static String pathSplitFormat(String filePath) {
        return filePath.replace("///", "/").replace("//", "/").replace("\\\\\\", "\\").replace("\\\\", "\\");
    }

    public static String getFileExtendName(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    public static String getFileNameNotExtend(String fileName) {
        return FilenameUtils.removeExtension(fileName);
    }

    public static File getLocalSaveFile(String fileUrl) {
        String localSavePath = getStaticPath() + fileUrl;
        return new File(localSavePath);
    }

    public static File getCacheFile(String fileUrl) {
        String cachePath = getStaticPath() + "cache" + File.separator + fileUrl;
        return new File(cachePath);
    }

    public static File getTempFile(String fileUrl) {
        String tempPath = getStaticPath() + "temp" + File.separator + fileUrl;
        File tempFile = new File(tempPath);
        File parentFile = tempFile.getParentFile();
        if (!parentFile.exists()) {
            boolean result = parentFile.mkdirs();
            if (!result) {
                // throw new RuntimeException()("创建temp目录失败：目录路径：" + parentFile.getPath());
            }
        }

        return tempFile;
    }

    public static File getProcessFile(String fileUrl) {
        String processPath = getStaticPath() + "temp" + File.separator + "process" + File.separator + fileUrl;
        File processFile = new File(processPath);
        File parentFile = processFile.getParentFile();
        if (!parentFile.exists()) {
            boolean result = parentFile.mkdirs();
            if (!result) {
                // throw new UFOPException("创建process目录失败：目录路径：" + parentFile.getPath());
            }
        }

        return processFile;
    }

    public static String getProjectRootPath() {
        String absolutePath = null;

        try {
            String url = ResourceUtils.getURL("classpath:").getPath();
            absolutePath = urlDecode((new File(url)).getAbsolutePath()) + File.separator;
        } catch (FileNotFoundException var2) {
            var2.printStackTrace();
        }

        return absolutePath;
    }

    public static String urlDecode(String url) {
        String decodeUrl = null;

        try {
            decodeUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
        }

        return decodeUrl;
    }

    public static String getStaticPath() {
        String localStoragePath = LOCAL_STORAGE_PATH;
        if (StringUtils.isNotEmpty(localStoragePath)) {
            return (new File(localStoragePath)).getPath() + File.separator;
        } else {
            String projectRootAbsolutePath = getProjectRootPath();
            int index = projectRootAbsolutePath.indexOf("file:");
            if (index != -1) {
                projectRootAbsolutePath = projectRootAbsolutePath.substring(0, index);
            }

            return (new File(projectRootAbsolutePath + "static")).getPath() + File.separator;
        }
    }

    public static String getUploadFileUrl(String identifier, String extendName) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        String path = "upload/" + formater.format(new Date()) + "/";
        File dir = new File(getStaticPath() + path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                // throw new UFOPException("创建upload目录失败：目录路径：" + dir.getPath());
            }
        }

        path = path + identifier + "." + extendName;
        return path;
    }

    public static String getAliyunObjectNameByFileUrl(String fileUrl) {
        if (fileUrl.startsWith("/") || fileUrl.startsWith("\\")) {
            fileUrl = fileUrl.substring(1);
        }

        return fileUrl;
    }

    public static String getParentPath(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }


    // public static String getFileType(String fileExt) {
    //     if ()
    // }
}
