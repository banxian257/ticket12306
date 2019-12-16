package com.wza.module.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wza.common.util.*;
import com.wza.module.entity.Logdevice;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    @Resource
    private ConfigService configService;
    @Value("${12306.account}")
    private String name;
    @Value("${12306.pwd}")
    private String pwd;

    /**
     * 登录 账号密码
     */
    public void login() {
        try {
 /*           // 获取动态秘钥
            //   Device.init();
            // 是否需要验证码登录
            boolean loginPassCode = false;
            HttpClientTool.doGetSSL(ApiUrl.loginInitPage, null, null);


            //获取登录的配置
            JSONObject loginConf = this.getLoginConf(ApiUrl.conf, getIndexInitHeaderMap());
            if (loginConf.getJSONObject("data").getString("is_login_passCode").equals("Y")) {
                loginPassCode = true;
            }
*/
            System.out.println("登录");
            //设置cookies
            setCookies();
            //获取验证码
            System.out.println("获取登录验证码");
            String img = Util.getCheckImg();
            //自动 识别 验证码
            String coordinate = OrcUtil.automaticRecognitionCheckCode(img);
            System.out.println("自动识别验证码坐标:" + coordinate);
            //请求校验验证码
            CheckCoordinate(coordinate);
            System.out.println("验证码识别通过:" + coordinate);
            Map<String, String> map = new HashMap<>();
            map.put("username", name);
            map.put("password", pwd);
            map.put("appid", "otn");
            map.put("answer", coordinate);
            /*    登录*/
            String loginResult = HttpClientTool.doPost(ApiUrl.login, getLoginHeader(), map);
            System.out.println(loginResult);
            //转发进入首页
            loginResult = HttpClientTool.doPost(ApiUrl.passport, getLoginHeader(), map);
            if (loginResult.equals("302")) {
                HttpClientTool.doGet(ApiUrl.userLogin, getLoginHeader(), null);
            }
      /*      JSONObject login = JSON.parseObject(loginResult);
               System.out.println(loginResult);*/

            //  checkOnline();


            configService.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取秘钥
     */
    public void secretKey() {
        String result = HttpClientTool.doGet("http://rail.51ab.top/api/getRailId", null, null);

    }


    /**
     * 基本头部
     */
    public Map<String, String> getHeaderMap() {
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


    public void CheckCoordinate(String coordinate) throws Exception {
        Map map = getHeaderMap();
        map.put("Referer", ApiUrl.queryInitPage);
        String content = HttpClientTool.doPost(String.format(ApiUrl.captchaCheck, coordinate, Math.random()), map, null);
        System.out.println(content);

    }


    //设置cookies
    public void setCookies() {
        Logdevice logdevice = LogdeviceUtil.getLogdevice();
        System.out.println("RAIL_EXPIRATION：" + logdevice.getExp());
        System.out.println("RAIL_DEVICEID：" + logdevice.getDfp());

        BasicClientCookie expiration = new BasicClientCookie("RAIL_EXPIRATION", logdevice.getExp());
        expiration.setDomain(ApiUrl.host);
        expiration.setPath("/");
        HttpClientTool.cookieStore.addCookie(expiration);
        BasicClientCookie deviceid = new BasicClientCookie("RAIL_DEVICEID", logdevice.getDfp());
        deviceid.setDomain(ApiUrl.host);
        deviceid.setPath("/");
        HttpClientTool.cookieStore.addCookie(deviceid);
    }

    /**
     * 本方法放弃
     * 用于获取cookies  (不懂不知道这 cookie 怎么生成的么)
     * 问题是 本半仙     不懂得如何根据 GetJS.js
     * 解析出algID 和hashCode
     *
     * @throws Exception
     */
    static void initRail() throws Exception {
        //获取RAIL_DEVICEID和RAIL_EXPIRATION
        //String getJsUrl = "https://kyfw.12306.cn/otn/HttpZF/GetJS";
        String resStr = HttpClientTool.doPost(ApiUrl.getRailUrl, null, null);

        String str = resStr.substring(resStr.indexOf("{"), resStr.indexOf("}") + 1);
        System.out.println(str);
        JSONObject obj = JSON.parseObject(str);
        //  Utils.addRailCookies(obj.getString("exp"), obj.getString("dfp"));
        System.out.println(obj.getString("exp") + obj.getString("dfp"));
    }


}
