package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.ocr.AipOcr;
import com.fx.pan.baidu.OcrClient;
import com.fx.pan.common.Msg;
import com.fx.pan.service.OcrService;
import com.fx.pan.utils.Md5Utils;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

/**
 * @Author leaving
 * @Date 2022/3/24 19:02
 * @Version 1.0
 */

@Service
public class OcrServiceImpl implements OcrService {

    // @Bean
    // BaiduFactory baiduFactory;

    @Override
    public Msg baiduGeneralOcr(byte[] readImageFile, String imagePath) {
        AipOcr client = OcrClient.getInstance();
        if (client == null) {
            return Msg.error(500, "文字识别服务创建失败");
        }
        System.out.println("百度client===" + client);
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");

        JSONObject res = client.basicGeneral(imagePath, options);
        // basicGeneral(bytesFile, options);
        String fmd = Md5Utils.md5HashCode32(new ByteArrayInputStream(readImageFile));

        return Msg.success("ok").put("result", JSON.parse(res.toString(2)));
        // .put("client", client.toString().substring(25, 33)).put("log_id", fmd);
    }
}
