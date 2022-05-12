package com.fx.pan.utils.file;

import com.fx.pan.utils.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leaving
 * @date 2022/3/10 18:44
 * @version 1.0
 */

public class FileTypeUtils {

    public static Map<String, String> PATH_MAP = new HashMap();
    public static String LOCAL_STORAGE_PATH;
    public static final String[] IMG_FILE = new String[]{"bmp", "jpg", "png", "tif", "gif", "jpeg"};
    public static final String[] DOC_FILE = new String[]{"doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "hlp",
            "wps", "rtf", "html", "pdf", "md"};
    public static final String[] VIDEO_FILE = new String[]{"avi", "mp4", "mpg", "mov", "swf"};
    public static final String[] BT_FILE = new String[]{"torrent"};
    public static final String[] Audio_FILE = new String[]{"wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr",
            "aac", "flac"};
    public static final String[] TXT_FILE = new String[]{"txt", "html", "java", "xml", "js", "css", "json"};
    public static final String[] ZIP_FILE = new String[]{"7z", "zip", "rar"};
    public static final String[] TORRENT_FILE = new String[]{"torrent"};
    public static final int IMAGE_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int ZIP_TYPE = 4;
    // public static final int BT_TYPE = 4;
    public static final int Audio_TYPE = 5;
    public static final int OTHER_TYPE = 6;

    public static final int SHARE_FILE = 8;
    public static final int RECYCLE_FILE = 7;

    public static List<String> getFileExtendsByType(int fileType) {

        List<String> fileExtends;
        switch (fileType) {
            case IMAGE_TYPE:
                fileExtends = Arrays.asList(IMG_FILE);
                break;
            case DOC_TYPE:
                fileExtends = Arrays.asList(DOC_FILE);
                break;
            case VIDEO_TYPE:
                fileExtends = Arrays.asList(VIDEO_FILE);
                break;
            case ZIP_TYPE:
                fileExtends = Arrays.asList(ZIP_FILE);
                break;
            case Audio_TYPE:
                fileExtends = Arrays.asList(Audio_FILE);
                break;
            default:
                fileExtends = new ArrayList<>();
                break;


        }
        System.out.println("分类文件扩展名集合=======" + fileExtends);
        return fileExtends;
    }

    public static int getFileTypeByExtendName(String extendName) {
        // 找不到匹配返回未知文件类型 9
        // 0目录,1图片,2文档,3视频,4压缩文件,5音频,6压缩文件 9未知
        int fileType = 9;
        if (Arrays.asList(IMG_FILE).contains(extendName)) {
            fileType = 1;
        } else if (Arrays.asList(DOC_FILE).contains(extendName)) {
            fileType = 2;
        } else if (Arrays.asList(VIDEO_FILE).contains(extendName)) {
            fileType = 3;
        } else if (Arrays.asList(ZIP_FILE).contains(extendName)) {
            fileType = 4;
        } else if (Arrays.asList(Audio_FILE).contains(extendName)) {
            fileType = 5;
        } else if (Arrays.asList(OTHER_TYPE).contains(extendName)) {
            fileType = 9;
        }
        return fileType;
    }

    /**
     * 判断是否为视频文件
     *
     * @param extendName 扩展名
     * @return 是否为视频文件
     */
    public static boolean isVideoFile(String extendName) {
        for (String extend : VIDEO_FILE) {
            if (extendName.equalsIgnoreCase(extend)) {
                return true;
            }
        }
        return false;
    }

    public static String getStaticPath() {
        String localStoragePath = LOCAL_STORAGE_PATH;
        if (StringUtil.isNotEmpty(localStoragePath)) {
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

    public static String getUploadFileUrl(String identifier, String extendName) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        String path = "upload/" + formater.format(new Date()) + "/";
        File dir = new File(getStaticPath() + path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                throw new RuntimeException("创建upload目录失败：目录路径：" + dir.getPath());
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

    /**
     * 获取不包含扩展名的文件名
     *
     * @param fileName 文件名
     * @return 文件名（不带扩展名）
     */
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

}
