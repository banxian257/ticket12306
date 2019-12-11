package com.wza.common.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

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

    public static int getInt(Object obj)
    {
        if (obj instanceof Integer)
        {
            return ((Integer) obj).intValue();
        }
        return isNull(obj) ? 0 : Integer.parseInt(obj.toString());
    }
}
