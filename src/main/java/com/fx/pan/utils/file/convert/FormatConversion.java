package com.fx.pan.utils.file.convert;

import com.aspose.cells.Workbook;
import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveFormat;
import com.fx.pan.common.Constants;
import com.fx.pan.domain.FileBean;
import com.fx.pan.utils.RedisCache;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;

/**
 * @author leaving
 * @date 2022/3/12 16:20
 * @version 1.0
 */

@Component
public class FormatConversion {

    private static RedisCache redisCache;

    @Resource
    private RedisCache rc;

    @PostConstruct
    public void init() {
        redisCache = rc;
    }


    public static void doc2pdf(String inPath, String outPath, FileBean fileBean, Long userId, String type, Long t) {
        FileOutputStream os = null;
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath); // 新建一个空白pdf文档
            os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            // EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            System.out.println("pdf转换成功，共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                            ":" + t,
                    100);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }




    @SneakyThrows
    public static boolean pdf2Doc(String filePath, String savePath, FileBean fileBean, Long userId, String type, Long t) {
        // spire实现
        File file = new File(filePath);
        // 创建Pdf工具类对象
        PdfDocument pdf = new PdfDocument();
        // 拼接Word文件名
        String projectPath = System.getProperty("user.dir");
        String name = file.getName();
        pdf.loadFromFile(filePath);

        //保存为Word格式
        String fileName = file.getName().substring(0, file.getName().lastIndexOf(".")) + ".docx";
        pdf.saveToFile(savePath, FileFormat.DOCX);

        InputStream is = new FileInputStream(savePath);
        XWPFDocument document = new XWPFDocument(is);
        //以上Spire.Doc 生成的文件会自带警告信息，这里来删除Spire.Doc 的警告
        document.removeBodyElement(0);
        //输出word内容文件流，新输出路径位置
        OutputStream os=new FileOutputStream(savePath);
        try {
            document.write(os);
            System.out.println("生成docx文档成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }



        // Apache实现 只能转成doc格式
        // try
        // {
        //     String pdfFile = filePath;
        //     // String docFile = savePath;
        //     // File file = new File(pdfFile);
        //     // if (!file.exists()) {
        //     //     file.createNewFile();
        //     // }
        //     PDDocument doc = PDDocument.load(new File(filePath));
        //     int pagenumber = doc.getNumberOfPages();
        //     pdfFile = pdfFile.substring(0, pdfFile.lastIndexOf("."));
        //     String fileName = pdfFile + ".doc";
        //     System.out.println("转换word路径：" + fileName);
        //     File file = new File(fileName);
        //     if (!file.exists())
        //     {
        //         file.createNewFile();
        //     }
        //     FileOutputStream fos = new FileOutputStream(fileName);
        //     Writer writer = new OutputStreamWriter(fos, "UTF-8");
        //     PDFTextStripper stripper = new PDFTextStripper();
        //     stripper.setSortByPosition(true);// 排序
        //     stripper.setStartPage(1);// 设置转换的开始页
        //     stripper.setEndPage(pagenumber);// 设置转换的结束页
        //     stripper.writeText(doc, writer);
        //     writer.close();
        //     doc.close();
        //     System.out.println("pdf转换word成功！");
        // }
        // catch (IOException e)
        // {
        //     e.printStackTrace();
        // }
        redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                        ":" + t,
                100);
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
}
