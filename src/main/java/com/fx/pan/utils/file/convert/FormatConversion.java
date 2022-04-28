package com.fx.pan.utils.file.convert;

import com.aspose.cells.Workbook;
import com.aspose.words.License;
import com.aspose.words.PdfSaveOptions;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @Author leaving
 * @Date 2022/3/12 16:20
 * @Version 1.0
 */

public class FormatConversion {

    @Value("${fx.absoluteFilePath}")
    private String absoluteFilePath;

    /**
     * 获取license 去除水印
     *
     * @return
     */
    public static boolean getLicense() {
        boolean result = false;
        InputStream is = null;
        try {
            Resource resource = new ClassPathResource("license.xml");
            if (resource.exists()) {
                is = resource.getInputStream();
            } else {
                System.out.println("License not found!");
                return false;
            }
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean Pdf2Doc(String filePath, String savePath) {
        File file = new File(filePath);
        // 创建Pdf工具类对象
        PdfDocument pdf = new PdfDocument();
        // File file =  new File(savePath+ file.getName());

        // 拼接Word文件名
        String projectPath = System.getProperty("user.dir");
        String name = file.getName();
        pdf.loadFromFile(filePath);

        //保存为Word格式
        String fileName = file.getName().substring(0, file.getName().lastIndexOf(".")) + ".docx";
        pdf.saveToFile(savePath, FileFormat.DOCX);
        return true;
    }

    /**
     * excel转pdf
     * @param filePath
     * @param savePath
     * @return
     */
    /**
     * excel 转为pdf 输出。
     *
     * @param sourceFilePath excel文件
     * @param desFilePath    pad 输出文件目录
     * @return
     */
    public static boolean excel2pdf(String sourceFilePath, String desFilePath) {
        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
            return false;
        }
        try {
            Workbook wb = new Workbook(sourceFilePath);// 原始excel路径
            System.out.println("原始excel路径：" + sourceFilePath);

            FileOutputStream fileOS = new FileOutputStream(desFilePath);
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            // pdfSaveOptions.setOnePagePerSheet(true);
            int[] autoDrawSheets = {3};
            //当excel中对应的sheet页宽度太大时，在PDF中会拆断并分页。此处等比缩放。
            // wb.autoDraw(wb,autoDrawSheets);
            int[] showSheets = {0};
            //隐藏workbook中不需要的sheet页。
            // printSheetPage(wb,showSheets);
            // wb.save(fileOS, pdfSaveOptions);
            wb.save(desFilePath);
            fileOS.flush();
            fileOS.close();
            //System.out.println("转换PDF完毕！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (new File(desFilePath).exists()) {
            System.out.println("转换PDF完毕！desFilePath==" + desFilePath);
            return true;
        } else {
            return false;
        }

    }

    // public static boolean Excel2Pdf(InputStream inputStream, String filePath, String savePath) {
    //     Workbook wb = new Workbook();
    //     //引入Excel文件
    //     wb.loadFromFile(filePath);
    //     //导出PDF文件
    //     // wb.saveToFile(savePath, FileFormat.PDF);
    //     wb.saveToFile(savePath, String.valueOf(FileFormat.PDF));
    //     return true;
    // }
}
