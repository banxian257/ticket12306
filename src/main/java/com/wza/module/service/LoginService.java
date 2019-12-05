package com.wza.module.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wza.common.util.ApiUrl;
import com.wza.common.util.HttpClient;
import com.wza.common.util.HttpClientTool;
import com.wza.module.entity.TicketConfig;
import com.wza.module.util.Utils;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.stereotype.Service;

import java.net.CookieStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {


    public static void main(String[] args) {

        System.out.println(new Date());
    }

    /**
     * 登录 账号密码
     * @param name
     * @param pwd
     */
    public void login(String name, String pwd) {
        try {

      /*      // 是否需要验证码登录
            boolean loginPassCode = false;
            HttpClientTool.doGetSSL(ApiUrl.loginInitPage, null, null);



            //获取登录的配置
            JSONObject loginConf = this.getLoginConf(ApiUrl.conf, getIndexInitHeaderMap());
            if (loginConf.getJSONObject("data").getString("is_login_passCode").equals("Y")) {
                loginPassCode = true;
            }
*/
            //登录需要验证码
            if (true) {
                //获取验证码
                System.out.println("获取登录验证码");
                String img = getCheckImg();
                //自动 识别 验证码
                String coordinate = automaticRecognitionCheckCode(img);
                System.out.println("自动识别验证码坐标:" + coordinate);
                //请求校验验证码
                CheckCoordinate(coordinate);
                System.out.println("验证码识别通过:" + coordinate);
                Map<String, String> map = new HashMap<>();
                map.put("username", name);
                map.put("password", pwd);
                map.put("appid", "otn");
                map.put("answer", coordinate);
            /*    Utils.initRail();*/
                String loginResult = HttpClientTool.doPost(ApiUrl.login, getLoginHeader(), map);
                if ("302".equals(loginResult)){
                     loginResult = HttpClientTool.doPost(ApiUrl.passport, getLoginHeader(), map);

                }
                //JSONObject login = JSON.parseObject(loginResult);
             //   System.out.println(loginResult);
                checkOnline();
                TicketConfig t=new TicketConfig();
                StationService.init();
                t.setDate("2019-11-26");
                t.setArrival(StationService.getCode("徐州东"));
                t.setDeparture(StationService.getCode("上海虹桥"));
                BuyTickets b=new BuyTickets();

             //   b.QueryTicket(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 基本头部
     */
    public  Map<String, String> getHeaderMap() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", ApiUrl.userAgent);
        header.put("Host", ApiUrl.host);
        header.put("Referer", ApiUrl.referer);
        return header;
    }

    /**
     * 登录校验头部map
     *
     * @return
     */
    public Map<String, String> getIndexInitHeaderMap() {
        Map<String, String> header = getHeaderMap();
        header.put("Origin", ApiUrl.baseUrl);
        header.put("Referer", ApiUrl.resources);
        return header;
    }

    /**
     * 登录头部
     *
     * @return map
     */
    public Map<String, String> getLoginHeader() {
        Map<String, String> header = getHeaderMap();
        header.put("Referer", ApiUrl.resources);
        header.put("Origin", ApiUrl.baseUrl);
        header.put("Accept", "application/json, text/javascript, */*; q=0.01");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-Language", "zh-CN,zh;q=0.9");
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        header.put("X-Requested-With", "XMLHttpRequest");
        header.put("Connection", "keep-alive");
        header.put("Sec-Fetch-Mode", "cors");
        header.put("Sec-Fetch-Site", "same-origin");
        return header;
    }

    /**
     * 校验登录状态
     *
     * @return
     */
    public static Map<String, Object> checkOnline() throws Exception {
        Map<String, String> body = new HashMap<>(1);
        body.put("appid", "otn");
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", ApiUrl.userAgent);
        header.put("Host", ApiUrl.host);
        header.put("Origin", ApiUrl.baseUrl);
        header.put("Referer", ApiUrl.dc);
        String result = HttpClientTool.doPost(ApiUrl.uamtkStatic, header, body);
        return JSON.parseObject(JSON.toJSONString(JSON.parseObject(result, Map.class)));
    }

    /**
     * 获取登录 相关配置
     *
     * @param url 地址
     * @return json
     */
    public JSONObject getLoginConf(String url, Map<String, String> headerMap) throws Exception {
        String result = HttpClientTool.doGetSSL(url, null, null);
        return JSON.parseObject(JSON.toJSONString(JSON.parseObject(result, Map.class)));

    }

    /**
     * 获取登录的图片验证码
     */
    public String getCheckImg() throws Exception {
        Map map = getHeaderMap();
        map.put("Referer", ApiUrl.queryInitPage);
        String content = HttpClientTool.doPost(String.format(ApiUrl.captchaImage, Math.random()), null, null);
        return JSON.parseObject(content).getString("image");
    }

    public void CheckCoordinate(String coordinate) throws Exception {
        Map map = getHeaderMap();
        map.put("Referer", ApiUrl.queryInitPage);
        String content = HttpClientTool.doPost(String.format(ApiUrl.captchaCheck, coordinate, Math.random()), map, null);
        System.out.println(content);

    }

    /**
     * 自动识别验证码
     *
     * @return 坐标
     */
    public String automaticRecognitionCheckCode(String img) throws Exception {
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
