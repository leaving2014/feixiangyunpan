package com.fx.pan.baidu;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Author leaving
 * @Date 2021/12/4 10:26
 * @Version 1.0
 */

public class baiduOCRtest {

    @Test
    void testOCRclient(){

        System.out.println(new OcrClient());
        System.out.println(new OcrClient());


    }

    @Test
    void testOCR() throws JSONException {
        AipOcr client = new AipOcr("11487965", "4RHdwmUxjMYSgSBcUHPhbiQm", "mGBce6uadRUtDvH1ZtYmqDrNewltWGKW");
        // AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");


        // 参数为本地图片路径
        // String image = "test.jpg";
        String image = "D:\\ideaWorkspace\\pan\\src\\main\\resources\\testOCR\\1.png";
        JSONObject res = client.basicGeneral(image, options);
        System.out.println(res.toString(2));

        // 参数为本地图片二进制数组
        // byte[] file = readImageFile(image);
        // res = client.basicGeneral(file, options);
        // System.out.println(res.toString(2));


        // 通用文字识别, 图片参数为远程url图片
        // JSONObject res = client.basicGeneralUrl(url, options);
        // System.out.println(res.toString(2));


    }
    public static byte[] readImageFile(String path) {
        // 使用 . 分割字符串需要使用 \\转义
        String[] pathArr = path.split("\\.");
        String imgType = pathArr[pathArr.length - 1];
        File f = new File(path);
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //经测试转换的图片是格式这里就什么格式，否则会失真
            ImageIO.write(bi, imgType, baos);
            byte[] bytes = baos.toByteArray();

            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
