package com.fx.pan.baidu;

import com.baidu.aip.ocr.AipOcr;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author leaving
 * @Date 2021/12/4 15:49
 * @Version 1.0
 */

/**
 * 百度ocrClient,生成client实例
 */

public class OcrClient {

    @Value("${baidu.ocr.APP_ID}")
    private static String app_id;
    @Value("${baidu.ocr.API_KEY}")
    private static String api_key;
    @Value("${baidu.ocr.SECRET_KEY}")
    private static String secret_key;
    // = new AipOcr(app_id, api_key,
    // secret_key);

    private static AipOcr aipOcr;

    private static AipOcr client = new AipOcr("11487965", "4RHdwmUxjMYSgSBcUHPhbiQm",
            "mGBce6uadRUtDvH1ZtYmqDrNewltWGKW");

    public static AipOcr getInstance() {
        // if (client == null) {
        //     synchronized (AipOcr.class) {
        //         if (client == null) {
        //             client = new AipOcr("11487965", "4RHdwmUxjMYSgSBcUHPhbiQm", "mGBce6uadRUtDvH1ZtYmqDrNewltWGKW");
        //         }
        //     }
        // }
        return client;
    }


    public static AipOcr getAipOcr() {
        if (aipOcr == null) {
            synchronized (AipOcr.class) {
                if (aipOcr == null) {
                    aipOcr = new AipOcr(app_id, api_key, secret_key);
                    // aipOcr = new AipOcr("11487965", "4RHdwmUxjMYSgSBcUHPhbiQm", "mGBce6uadRUtDvH1ZtYmqDrNewltWGKW");
                }
            }
        }
        System.out.println("OCRClient:::getAipOcr===========" + aipOcr);
        return aipOcr;
    }
}
