package com.fx.pan.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.service.OfflineDownloadService;
import com.fx.pan.utils.OfflineDownload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author leaving
 * @Date 2021/12/18 15:24
 * @Version 1.0
 */
@RequestMapping("/offline")
@RestController
public class OfflineDownloadController {

    @Resource
    private OfflineDownloadService offlineDownloadService;


    @PostMapping("/new")
    public Msg newOfflineDownload(@RequestParam("url") String url,
                                  HttpServletResponse response) throws IOException {
        // String fileName = FileUtils.getFileName(url);
        Msg msg = offlineDownloadService.downloadFromUrl(url);
        // Msg msg1 = OfflineDownload.downLoadFromUrl(url, "D:\\ideaWorkspace\\pan\\src\\main\\resources" +
        //         "\\offlinedownload");

        return msg;

    }

    public static void inputStream2File(InputStream is, File file) {
        OutputStream os = null;
        //https://developer5.baidupan.com/121916bb/2018/07/05/ad1a17747bdb84ddccfbc36babea8900
        // .rar?st=LUF6kBnovBszMOTu2MklYQ&e=1639904244&b
        // =A7QMgACXWbhXuAP5Cr5T6FKmD7UFhVf_bA7gJhVDbXrgH7QqQAOZR3gPWX_b8FyQSvVeUA8QOkBepSmA6hUHdUewMODGoAaVkwV2cDJgp0UyVSZg8h&fi=3995631&pid=210-82-53-194&up=2&mp=0&pim=9bb0fb5d994a51c30e1f3531b5687d3e&co=1
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Logger.getLogger(OfflineDownloadController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @PostMapping("/status")
    public void downloadStatus(@RequestParam("t") String string, HttpServletResponse response) {

    }
}
