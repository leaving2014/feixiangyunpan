package com.fx.pan.controller;

import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Office操作
 *
 * @Author leaving
 * @Date 2022/3/16 20:48
 * @Version 1.0
 */

@Tag(name = "office", description = "该接口为ffice文件操作接口，主要用来做一些文档的编辑，浏览等。")
@Slf4j
@RequestMapping("/office")
public class OfficeController {
    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @GetMapping( "/excel")
    // @ResponseBody
    // @RequestParam("id") long id, @RequestParam("status") String status, HttpServletResponse response
    public ResponseEntity<byte[]> excelData(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("excel请求数据");
        // Long time, String id, int fileType, String extensionName
        // Date d = new Date(time);
        // SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        // String date = sf.format(d);
        //
        String filePath;
        byte[] bytes = new byte[0];
        // response.setHeader("Accept-Ranges", "bytes");

        // filePath = absoluteFilePath + "/" + date + "/" + id + "." + extensionName;

        filePath = "D:\\ideaWorkspace\\pan\\static\\file\\20220321\\b703fe71087c8533c2bf9fefdf3058eb.xlsx";
        File file = new File(filePath);
        JsonObject jsonObject = new JsonObject();
        try {

            String redeemCodeString = "";
            HttpHeaders headers = new HttpHeaders();
            response.reset();
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            // response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            // response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            outputStream.write(FileUtils.readFileToByteArray(file), 0, FileUtils.readFileToByteArray(file).length);
            // outputStream.write(redeemCodeString.getBytes(), 0, redeemCodeString.getBytes().length);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
            // outputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.addProperty("msg", "系统异常！");
            jsonObject.addProperty("code", 1);
        }
        return null;
    }


    @GetMapping("/excel/data")
    public ResponseEntity<byte[]> excel() throws IOException {
        // XSSFWorkbook xb = new XSSFWorkbook();
        XSSFWorkbook xb = new XSSFWorkbook();
        XSSFSheet sheet = xb.createSheet();
        XSSFCellStyle style = xb.createCellStyle();
        // HSSFWorkbook wb = new HSSFWorkbook();
        // HSSFSheet sheet = wb.createSheet();
        // HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor((short) 13);
        //设置单元格前景色
        style.setFillForegroundColor((short) 13);
        //设置图案样式
        // style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        // style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        // style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // HSSFFont font = wb.createFont();
        XSSFFont font = xb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 16);
        // font.setBoldweight(XSSFCellStyle.BOLDWEIGHT_BOLD);
        // style.setFont(font);
        //设置单元格合并
        //Region region = new Region(0,(short) 0,0,(short) 6);
        //sheet.addMergedRegion(region);
        for (int rownum = 0; rownum < 50; rownum++) {
            // HSSFRow hssfRow = sheet.createRow(rownum);
            XSSFRow xssfRow = sheet.createRow(rownum);
            for (int cellnum = 0; cellnum < 30; cellnum++) {
                XSSFCell cell = xssfRow.createCell((short) cellnum);
                cell.setCellValue(rownum + "" + cellnum);
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            xb.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        String fileName = new String("测试.xlsx".getBytes("UTF-8"), "iso-8859-1");
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ResponseEntity<byte[]> filebyte = new ResponseEntity<byte[]>(outputStream.toByteArray(), httpHeaders,
                HttpStatus.CREATED);
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
        return filebyte;
    }
}
