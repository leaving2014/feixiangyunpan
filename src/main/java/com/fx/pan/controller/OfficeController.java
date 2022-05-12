package com.fx.pan.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.domain.ExcelBean;
import com.fx.pan.domain.FileBean;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.service.ExcelService;
import com.fx.pan.service.FileService;
import com.fx.pan.utils.Base64Util;
import com.fx.pan.utils.SecurityUtils;
import com.fx.pan.utils.office.ExcelUtils;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Office操作
 *
 * @author leaving
 * @date 2022/3/16 20:48
 * @version 1.0
 */

@Tag(name = "office", description = "该接口为ffice文件操作接口，主要用来做一些文档的编辑，浏览等。")
@Slf4j
@RequestMapping("/office")
@RestController
public class OfficeController {
    @Value("${fx.absoluteFilePath}")
    String absoluteFilePath;

    @Resource
    private FileService fileService;

    @Resource
    private ExcelService excelService;

    @GetMapping("/excel")
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
            // response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;
            // charset=utf-8");
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


    @GetMapping("/excel/data1")
    public String excel() throws Exception {
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
        // return filebyte;
        String filePath = "D:/ideaWorkspace/pan/static/file/20220321/b703fe71087c8533c2bf9fefdf3058eb.xlsx";
        return Base64Util.encodeBase64File(filePath);
    }

    @PostMapping("/excel/info")
    public ResponseResult excelInfo(@RequestParam Long id) throws Exception {
        FileBean fileBean = fileService.selectFileById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("file", fileBean);
        return ResponseResult.success("获取成功",map);
    }

    /**
     * 创建在线表格
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/excel/create/online")
    public ResponseResult createExcelOnline() {
        Long userId = SecurityUtils.getUserId();
        String identifier = UUID.randomUUID().toString().replace("-", "");
        ExcelBean excelBean = new ExcelBean();
        excelBean.setUserId(userId);
        excelBean.setIdentifier(identifier);
        excelBean.setType(2);
        excelBean.setCreateTime(new Date());
        excelBean.setUpdateTime(new Date());
        boolean save = excelService.save(excelBean);
        if (save) {
            Map<String, Object> map = new HashMap<>();
            map.put("sheet", excelBean);
            return ResponseResult.success("在线表格创建成功",map);
        } else {
            return ResponseResult.error(500, "保创建失败");
        }
    }

    @PostMapping("/excel/collaborate")
    public ResponseResult excelCollaborate(@RequestParam Long id, @RequestParam Integer collaborate, @RequestParam Long fid) throws Exception {
        Long userId = SecurityUtils.getUserId();
        ExcelBean excelBean = excelService.getById(id);
        FileBean fileBean = fileService.selectFileById(fid);
        ExcelBean createExcel = null;
        if (collaborate == 1) {
            if (excelBean == null) {
                createExcel = excelService.createCoEditingByFile(fileBean, null);
                System.out.println("创建excel对象====" + createExcel);
                // BeanCopyUtils.copyBean(excelService.createCoEditingByFile(fileBean,null), ExcelBean.class);
            } else {
                createExcel = excelBean;
            }
            System.out.println("完成创建createExcel===========" + createExcel);

            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", createExcel.getId());
            updateWrapper.set("collaborate", collaborate);
            boolean update = excelService.update(updateWrapper);

            if (update) {
                Map<String, Object> map = new HashMap<>();
                map.put("sheet", createExcel);
                map.put("status", true);
                map.put("create", true);
                map.put("eid", createExcel.getId());
                return ResponseResult.success("协作成功",map);
            } else {
                return ResponseResult.error(500, "开启协作失败");
            }

        } else {
            // 关闭协作
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", id);
            updateWrapper.set("collaborate", collaborate);
            boolean update = excelService.update(updateWrapper);
            if (update) {
                Map<String, Object> map = new HashMap<>();
                map.put("sheet", excelBean);
                map.put("status", false);
                return ResponseResult.success("取消协作成功",map);
            } else {
                return ResponseResult.error(500, "取消协作失败");
            }
        }

    }

    @GetMapping("/excel/data")
    public String ExcelData(@RequestParam Long id) throws Exception {
        Long userId = SecurityUtils.getUserId();
        FileBean fileBean = fileService.selectFileById(id);
        String filePath = fxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
        return Base64Util.encodeBase64File(filePath);
    }

    @PostMapping("/excel/online")
    public ResponseResult excelOnline(@RequestParam Long id, @RequestParam String data) throws Exception {
        Long userId = SecurityUtils.getUserId();
        FileBean fileBean = fileService.selectFileById(id);
        String filePath = fxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
        Map map = new HashMap();
        map.put("file", data);
        return ResponseResult.success("同步成功",map);
    }

    @PostMapping("/excel/export")
    public ResponseResult downExcelFile(@RequestParam(value = "excelData") String excelData, @RequestParam Long id,
                                        HttpServletRequest request, HttpServletResponse response) {
        Long userId = SecurityUtils.getUserId();
        FileBean fileBean = fileService.selectFileById(id);
        excelData = excelData.replace("&#xA;", "\\r\\n");//去除luckysheet中 &#xA 的换行
        ExcelUtils.exportLuckySheetXlsx(excelData, request, response);
        Map map = new HashMap();
        map.put("file", fileBean);
        return ResponseResult.success("导出成功",map);
        // return ResponseResult.success("下载成功");
    }

    @PostMapping("/excel/online/data")
    public String excelOnlineData(@RequestParam Long id) throws Exception {
        // Long userId = SecurityUtils.getUserId();
        ExcelBean excelBean = excelService.getById(id);
        if (excelBean == null) {
            return "";
        } else {
            if (excelBean.getData() == null) {
                File file = null;
                try {
                    file = new File(URLDecoder.decode(ResourceUtils.getURL("classpath:static/newXlsx.json").getPath(),
                            "utf-8"));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return ExcelUtils.txtToString(new File(file.getAbsolutePath()));
            } else {
                return excelBean.getData();
            }

        }
    }

    @GetMapping("/excel/check")
    public Object checkExcel(@RequestParam Long id) throws Exception {
        Long userId = SecurityUtils.getUserId();
        ExcelBean excelBean = excelService.checkExist(id);
        System.out.println("excelBean:" + excelBean);

        if (excelBean == null) {
            FileBean fileBean = fileService.selectFileById(id);
            String filePath = fxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
            Map map = new HashMap();
            map.put("file", fileBean);
            return ResponseResult.success("获取成功",map);
            // put("data", Base64Util.encodeBase64File(filePath)).put("type", "file")
        } else {
            Object parse = null;
            if (excelBean.getData() != null) {
                String excelData = excelBean.getData().replace("\\", "");
                parse = JSON.parse(excelData);
            }
            excelBean.setData("");
            Map map = new HashMap();
            map.put("data", parse);
            map.put("excel", excelBean);
            map.put("type", "excel");

            return ResponseResult.success("获取成功",map);
        }
        //
        // FileBean fileBean = fileService.selectFileById(id);
        // System.out.println("fileBean====:" + fileBean);
        // if (fileBean == null) {
        //     ExcelBean excelBean = excelService.checkExist(id);
        //     if (excelBean == null) {
        //         return ResponseResult.error(0,"在线文档未创建");
        //     }else {
        //         return ResponseResult.success("获取成功").put("data", excelBean.getData());
        //     }
        // } else {
        //     String filePath = fxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
        //     return Base64Util.encodeBase64File(filePath);
        //
        //     // return ResponseResult.success("获取成功").put("data", excelBean.getData());
        //
        // }
    }

    @PostMapping("/excel/save")
    public ResponseResult saveExcelData(@RequestParam Long id, @RequestParam String sheetData) throws Exception {
        boolean b = excelService.updateExcelData(id, sheetData);
        if (b) {
            return ResponseResult.success("保存成功");
        } else {
            return ResponseResult.error(500, "保存失败");
        }
    }


    @PostMapping("/excel/update")
    public ResponseResult updateExcel(@RequestParam Long id, @RequestParam String data, @RequestParam Integer type) throws Exception {
        Long userId = SecurityUtils.getUserId();
        FileBean fileBean = fileService.selectFileById(id);
        ExcelBean excelBean;
        excelBean = excelService.selectByFileId(id);
        if (type == 1) {
            if (excelBean == null) {
                excelBean = new ExcelBean();
                excelBean.setFileId(id);
                excelBean.setTitle(fileBean.getFileName());
                excelBean.setData(data);
                excelBean.setIdentifier(fileBean.getIdentifier());
                excelBean.setType(1);
                excelBean.setCreateTime(new Date());
                excelBean.setUpdateTime(new Date());
                excelService.save(excelBean);
            } else {
                boolean b = excelService.updateExcelData(id, data);
            }
        } else {
            String filePath = fxUtils.getStaticPath() + "/" + fileBean.getFileUrl();
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        }

        Map map = new HashMap();
        map.put("file", fileBean);
        return ResponseResult.success("更新成功",map);
    }

}
