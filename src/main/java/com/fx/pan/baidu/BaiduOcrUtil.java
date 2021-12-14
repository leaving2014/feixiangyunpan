package com.fx.pan.baidu;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.ocr.AipOcr;
import com.fx.pan.common.Msg;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.HashMap;

import static com.fx.pan.utils.FileUtils.readImageFile;

/**
 * 百度OCR工具类
 */
public class BaiduOcrUtil {

    @Value("${spring.baidu.ocr.APP_ID}")
    private static String app_id;
    @Value("${spring.baidu.ocr.API_KEY}")
    private static String api_key;
    @Value("${spring.baidu.ocr.SECRET_KEY}")
    private static String secret_key;

    //设置APPID/AK/SK
    public static final String APP_ID = app_id;
    public static final String API_KEY = api_key;
    public static final String SECRET_KEY = secret_key;


    /**
     * 通用文字识别
     * @return
     */
    public static Msg baiduGeneralOCR() {
        AipOcr client = OcrClient.getInstance();
        System.out.println(client);

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        //行置信度信息；如果输入参数 probability = true 则输出
        // options.put("probability", "true");


        // 参数为本地图片路径
        // String image = "test.jpg";
        String image = "D:\\ideaWorkspace\\pan\\src\\main\\resources\\testOCR\\3.png";
        // JSONObject res = client.basicGeneral(image, options);
        // System.out.println(res.toString(2));

        // 参数为本地图片二进制数组
        byte[] file = readImageFile(image);
        JSONObject res = client.basicGeneral(file, options);
        // System.out.println(res.toString(2));

        // 通用文字识别, 图片参数为远程url图片
        // JSONObject res = client.basicGeneralUrl(url, options);
        // System.out.println(res.toString(2));

        return Msg.success("ok").put("ocr_res", JSON.parse(res.toString(2))).put("client",
                client.toString().substring(25, 33));

    }


    /**
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Msg baiduGeneralOCR(String filePath) throws IOException {
        AipOcr client = OcrClient.getInstance();
        System.out.println(client);

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        //行置信度信息；如果输入参数 probability = true 则输出
        // options.put("probability", "true");


        byte[] bytesFile = readImageFile(filePath);
        JSONObject res = client.basicGeneral(bytesFile, options);

        return Msg.success("ok").put("ocr_res", JSON.parse(res.toString(2))).put("client",
                client.toString().substring(25, 33));
    }


    /**\
     *
     * @param bytesFile
     * @return
     * @throws IOException
     */
    public static Msg baiduGeneralOCR(byte[] bytesFile) throws IOException {
        AipOcr client = OcrClient.getInstance();
        System.out.println(client);

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");

        JSONObject res = client.basicGeneral(bytesFile, options);
        return Msg.success("ok").put("ocr_res", JSON.parse(res.toString(2))).put("client",
                client.toString().substring(25, 33));
    }
}
