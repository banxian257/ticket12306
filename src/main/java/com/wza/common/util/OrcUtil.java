package com.wza.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码识别
 *
 * @author 半仙
 */

public class OrcUtil {

    /**
     * 验证码识别，360接口
     *
     * @param base64 验证码
     * @return x ,y
     */
    public static String getImgPositionBy360(String base64) {
        try {
            Map<String, String> param = new HashMap<String, String>();
            param.put("base64", base64);
            String checkResult = HttpClientTool.doPost(ApiUrl.getCheck, null, param);
            JSONObject check = JSON.parseObject(checkResult);
            if (check != null && check.getBooleanValue("success")) {
                Header header0 = new BasicHeader("Host", "check.huochepiao.360.cn");
                Header header1 = new BasicHeader("Upgrade-Insecure-Requests", "1");
                Header header2 = new BasicHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36 QIHU 360EE");
                Header header3 = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
                StringEntity stringEntity = new StringEntity(
                        "{\"check\":\"" + check.getJSONObject("data").getString("check") + "\",\"img_buf\":\"" + base64
                                + "\",\"logon\":" + 1 + ",\"type\":\"D\"}",
                        "utf-8");
                stringEntity.setContentType("application/json;charset=UTF-8");

                HttpUriRequest getVcode = RequestBuilder.post().setUri(new URI(ApiUrl.getPositionBy360))
                        .setEntity(stringEntity).addHeader(header0).addHeader(header1).addHeader(header2)
                        .addHeader(header3).build();
                CloseableHttpClient httpClient = HttpClientTool.createSSLClientDefault();
                CloseableHttpResponse response = httpClient.execute(getVcode);
                HttpEntity entity1 = response.getEntity();
                String content1 = EntityUtils.toString(entity1, "UTF-8");
                if (content1.equals("error")) {
                    return null;
                } else {
                    JSONObject obj = JSON.parseObject(content1);
                    return obj.getString("res").replaceAll("\\(", "").replaceAll("\\)", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 验证码识别，AI打码
     *
     * @param base64 验证码
     * @return x ,y
     */
    public static String getImgPositionByAi(String base64) throws Exception {
        try {
            Map<String, String> param = new HashMap<String, String>();
            param.put("base64", base64);
            String positionResult = HttpClientTool.doPost(ApiUrl.getPositionByAi, null, param);
            if (positionResult.equals("error")) {
                System.out.println("完犊子了验证码识别出错...");
                return null;
            } else {
                JSONObject obj = JSON.parseObject(positionResult);
                return obj.getString("res").replaceAll("\\(", "").replaceAll("\\)", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自动识别验证码
     *
     * @return 坐标
     */
    public static String automaticRecognitionCheckCode(String img) throws Exception {
        Map<String, String> body = new HashMap<>(1);
        body.put("img", img);
        String result = HttpClientTool.doPost(ApiUrl.automaticRecognition, null, body);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getString("msg").equals("success")) {
            String[] position = jsonObject.getString("result").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < position.length; i++) {
                String ent = position[i];
                if (ent.equals("1")) {
                    sb.append("(31,45),");
                } else if (ent.equals("2")) {
                    sb.append("(100,45),");
                } else if (ent.equals("3")) {
                    sb.append("(170,45),");
                } else if (ent.equals("4")) {
                    sb.append("(240,45),");
                } else if (ent.equals("5")) {
                    sb.append("(30,115),");
                } else if (ent.equals("6")) {
                    sb.append("(100,115),");
                } else if (ent.equals("7")) {
                    sb.append("(170,115),");
                } else if (ent.equals("8")) {
                    sb.append("(240,115),");
                }
            }
            String xy = sb.toString().substring(0, sb.toString().length() - 1);
            return xy.replaceAll("\\(", "").replaceAll("\\)", "");
        } else {
            return "";
        }
    }

}
