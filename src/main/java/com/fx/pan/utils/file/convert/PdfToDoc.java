package com.fx.pan.utils.file.convert;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * @Author leaving
 * @Date 2022/3/12 16:20
 * @Version 1.0
 */

public class PdfToDoc {

    @Value("${fx.absoluteFilePath}")
    private String absoluteFilePath;

    public boolean covert(File file, String savePath) {
        // 创建Pdf工具类对象
        PdfDocument pdf = new PdfDocument();
        // File file =  new File(savePath+ file.getName());

        // 拼接Word文件名
        String projectPath = System.getProperty("user.dir");
        String name = file.getName();
        pdf.loadFromFile(projectPath + "/" + name);

        //保存为Word格式
        String fileName = file.getName().substring(0, file.getName().lastIndexOf(".")) + ".docx";
        pdf.saveToFile(fileName, FileFormat.DOCX);
        return true;


    }
}
