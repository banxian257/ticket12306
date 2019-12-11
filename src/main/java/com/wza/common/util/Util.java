package com.wza.common.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class Util {
    /**
     * 获取登录的图片验证码
     */
    public static String getCheckImg() throws Exception {
        Map<String, String>  header = new HashMap<>();
        header.put("User-Agent", ApiUrl.userAgent);
        header.put("Host", ApiUrl.host);
        header.put("Referer", ApiUrl.queryInitPage);
        String content = HttpClientTool.doPost(String.format(ApiUrl.captchaImage, Math.random()), null, null);
        return JSON.parseObject(content).getString("image");
    }
}
