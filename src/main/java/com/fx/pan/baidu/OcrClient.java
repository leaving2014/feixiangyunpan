package com.fx.pan.baidu;

import com.baidu.aip.ocr.AipOcr;

/**
 * @Author leaving
 * @Date 2021/12/4 15:49
 * @Version 1.0
 */

/**
 * 百度ocrClient,生成client实例
 */

public class OcrClient {

    private  static AipOcr client = new AipOcr("11487965", "4RHdwmUxjMYSgSBcUHPhbiQm",
            "mGBce6uadRUtDvH1ZtYmqDrNewltWGKW");

    public static AipOcr getInstance(){
        return client;
    }
}
