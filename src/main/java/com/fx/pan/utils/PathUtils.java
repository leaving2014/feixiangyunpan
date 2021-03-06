package com.fx.pan.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;

import java.io.File;

/**
 * @author leaving
 * @date 2021/12/14 9:13
 * @version 1.0
 */

public class PathUtils {

    @Value("${fx.localStoragePath}")
    private String localStoragePath;


    /**
     * 获取服务器存放文件的目录路径
     * @return 目录路径（String)
     */
    public static String getFileDir(){
        String path= ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1)+"static/file";
        // System.out.println("系统文件存路径:"+path);
        File dir=new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return path;
    }
}
