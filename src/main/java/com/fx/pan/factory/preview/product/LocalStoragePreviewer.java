package com.fx.pan.factory.preview.product;

import com.fx.pan.factory.domain.ThumbImage;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.factory.preview.Previewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @Author leaving
 * @Date 2022/3/10 15:01
 * @Version 1.0
 */

public class LocalStoragePreviewer extends Previewer {
    public LocalStoragePreviewer(){

    }
    public LocalStoragePreviewer(ThumbImage thumbImage) {
        setThumbImage(thumbImage);
    }

    @Override
    public InputStream getInputStream(String fileUrl) {
        //设置文件路径
        File file = fxUtils.getLocalSaveFile(fileUrl);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;

    }
}
