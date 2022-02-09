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
    private  static AipOcr client = new AipOcr(app_id, api_key,
            secret_key);

    // private  static AipOcr client = new AipOcr("11487965", "4RHdwmUxjMYSgSBcUHPhbiQm",
    //         "mGBce6uadRUtDvH1ZtYmqDrNewltWGKW");
    public static AipOcr getInstance(){
        return client;
    }
}
