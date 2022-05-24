package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.ocr.AipOcr;
import com.fx.pan.domain.ResponseResult;
import com.fx.pan.service.BaiduOcrService;
import com.fx.pan.utils.Md5Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leaving
 * @date 2022/5/8 19:59
 * @version 1.0
 */
@Service
public class BaiduOcrServiceImpl implements BaiduOcrService {

    @Autowired
    private AipOcr aipOcr;

    public ResponseResult baiduGeneralOcr(byte[] readImageFile, String imagePath) {
        if (aipOcr == null) {
            return ResponseResult.error(500, "文字识别服务创建失败");
        }
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        JSONObject res;
        if (imagePath.equals("")) {
             res = aipOcr.basicGeneral(readImageFile, options);
        } else {
             res = aipOcr.basicGeneral(imagePath, options);
        }

        // basicGeneral(bytesFile, options);
        String fmd = Md5Utils.md5HashCode32(new ByteArrayInputStream(readImageFile));
        Map map = new HashMap();
        map.put("result", JSON.parse(res.toString(2)));
        return ResponseResult.success(map);
        // .put("client", client.toString().substring(25, 33)).put("log_id", fmd);
    }
}
