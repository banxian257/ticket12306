package com.wza.module.service;

import com.alibaba.fastjson.JSON;
import com.wza.common.util.ApiUrl;
import com.wza.common.util.HttpClientTool;
import com.wza.module.entity.TicketConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BuyTickets {
    private String date;
    private String departure;
    private String arrival;
    private Map<String, String> heards;
    /**
     * 查询剩余车票
     */
    public  void QueryTicket(TicketConfig config) {
        Map<String, String> map = new HashMap<>();
        map.put("User-Agent", ApiUrl.userAgent);
        map.put("Host", ApiUrl.host);
        map.put("Referer", ApiUrl.referer);
        map.put("X-Requested-With", "XMLHttpRequest");
        map.put("Referer", ApiUrl.queryInitPage);

        String result = HttpClientTool.doGetSSL(String.format(ApiUrl.leftTicket, config.getDate(),
                config.getDeparture(), config.getArrival()), map, null);

        Map data = (Map) JSON.parseObject(result, Map.class).get("data");
        if (!CollectionUtils.isEmpty(data)) {

        }
        reserveTicket();
    }

    /**
     * 预定车票
     */
    public void reserveTicket() {
        try {
            initHeards();
            LoginService.checkOnline();
            String token = initDc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成订单时需要先请求这个页面
     *
     * @return result
     */
    private String initDc()throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("_json_att", "");
        String result = HttpClientTool.doPost(ApiUrl.initDc, heards, map);
        Pattern p = Pattern.compile("globalRepeatSubmitToken \\= '(.*?)';");
        Matcher m = p.matcher(result);
        String token = "";
        while (m.find()) {
            token = m.group(1);
        }
        Pattern p1 = Pattern.compile("'key_check_isChange':'(.*?)',");
        Matcher m1 = p1.matcher(result);
        while (m1.find()) {
            token += "," + m1.group(1);
        }
        System.out.println(token);
        return token;
    }

    public void initHeards() {
        heards = new HashMap<>();
        heards.put("User-Agent", ApiUrl.userAgent);
        heards.put("Host", ApiUrl.host);
        heards.put("Referer", "https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc");
        heards.put("Accept", "*/*");
        heards.put("Accept-Encoding", "gzip, deflate");
        heards.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3");
        heards.put("Content-Type", "application/x-www-form-urlencoded");
        heards.put("Origin", ApiUrl.baseUrl);
    }

    /**
     * 获取乘客信息
     */
    public void initPassengerInfo() {

    }
}
