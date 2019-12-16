package com.wza.module.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wza.common.constant.Constant;
import com.wza.common.util.*;
import com.wza.module.entity.HttpProxy;
import com.wza.module.entity.SeatType;
import com.wza.module.entity.TicketConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.util.CollectionUtils;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyTickets {
    private Map<String, String> headers;
    private TicketConfig ticketConfig;
    private String oldPassengerStr;
    private String passengerTicketStr;
    private BasicCookieStore basicCookieStore = new BasicCookieStore();
    private HttpProxy httpProxy;

    /**
     * 查询剩余车票
     */
    public void QueryTicket(TicketConfig ticketConfig) {
        try {
            this.ticketConfig = ticketConfig;
            Map<String, String> header = new HashMap<>();
            header.put("User-Agent", ApiUrl.userAgent);
            header.put("Host", ApiUrl.host);
            header.put("Referer", ApiUrl.referer);
            header.put("X-Requested-With", "XMLHttpRequest");
            header.put("Referer", ApiUrl.queryInitPage);
            if (StationService.getCode(ticketConfig.getDeparture()) == null || StationService.getCode(ticketConfig.getArrival()) == null) {
                System.out.println("请输入正确的出发站或到达站");
                System.exit(0);
            }
            httpProxy = ProxyCache.getHttpProxy();
            String result = "";
            if (httpProxy == null) {
                String url = String.format(ApiUrl.leftTicketByCdn, Constant.queryTicket, ticketConfig.getDate(),
                        StationService.getCode(ticketConfig.getDeparture()), StationService.getCode(ticketConfig.getArrival()));
                result = HttpClientTool.doGetSSL(url, header, null);
            } else {
                HttpHost proxy = new HttpHost(httpProxy.getIp(), httpProxy.getPort());
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)
                        .setConnectionRequestTimeout(3000).setSocketTimeout(3000).setProxy(proxy).build();
                String url = String.format(ApiUrl.leftTicketByCdn, Constant.queryTicket, ticketConfig.getDate(),
                        StationService.getCode(ticketConfig.getDeparture()), StationService.getCode(ticketConfig.getArrival()));
                result = HttpClientTool.doGetSSL(url, header, null, requestConfig, basicCookieStore);
            }


            Map data = (Map) JSON.parseObject(result, Map.class).get("data");
            if (!CollectionUtils.isEmpty(data)) {
                List<String> arr = (List<String>) data.get("result");
                // 解析车次信息
                Map<String, Map<String, String>> ticketMap = new ConcurrentHashMap<String, Map<String, String>>();
                analysisTicket(arr, ticketMap);
                //指定车次
                List<String> trainNumber = new ArrayList<>();
                if (StringUtils.isNotBlank(ticketConfig.getTrainNumbers())) {
                    trainNumber = Arrays.asList(ticketConfig.getTrainNumbers().split(","));
                } else { //没有指定车次 就默认所有车次
                    Set<String> keys = ticketMap.keySet();
                    trainNumber.addAll(keys);
                }

                Collections.sort(trainNumber);
                //解析 信息
                List<Map<String, String>> surplusTicket = getSecretStr(ticketMap, trainNumber, ticketConfig.getSeats().split(","));
                for (Map<String, String> ticket : surplusTicket) {
                    String ticketCode = ticket.get("chehao");
                    String tobuySeat = ticket.get("toBuySeat");
                    //判断是否上了黑名单
                    if (Constant.blacklist.get(ticketCode + "_" + tobuySeat) == null) {
                        System.out.println("啦啦啦有余票啦");
                        initHeards();
                        Constant.bookQueue.put(ticket);
                        reserveTicket(tobuySeat);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取车次str (乘客多个，余票不足 忽略余票)
     *
     * @param map    map
     * @param trains trains
     * @param seats  seats
     * @return
     */
    public List<Map<String, String>> getSecretStr(Map<String, Map<String, String>> map, List<String> trains,
                                                  String[] seats) {
        String[] mats = seats;
        List<Map<String, String>> surplusTicket = new ArrayList<Map<String, String>>();
        for (String train : trains) {
            Map<String, String> seatsMap = map.get(train);
            if (null != seatsMap) {
                for (String mat : mats) {
                    String cnt = seatsMap.get(mat);
                    String secretStr = seatsMap.get("secret");
                    if (null != cnt && !"无".equals(cnt) && !"".equals(cnt)) {
                        if ("".equals(secretStr)) {
                            System.out.println(train + " " + mat + "未开售");
                        } else if (!"有".equals(cnt) && Integer.parseInt(cnt) < ticketConfig.getUserNames().split(",").length) {
                            System.out.println(train + " " + mat + " 有票：" + cnt + "但是不够"
                                    + "个,忽略");
                        } else {
                            seatsMap.put("toBuySeat", mat);
                            surplusTicket.add(seatsMap);
                        }
                    } else {
                        System.out.println(train + " " + mat + "暂无余票");
                    }
                }

            }
        }

        return surplusTicket;
    }

    /**
     * 解析车次信息
     *
     * @param checi
     * @param map
     */
    public void analysisTicket(List<String> checi, Map<String, Map<String, String>> map) {
        map.clear();

        /*
         * 车次：3 train_no 2 start_station_code:起始站：4 end_station_code终点站：5
         * from_station_code:出发站：6 to_station_code:到达站：7
         *
         * start_time:出发时间：8 arrive_time:达到时间：9
         *
         * 历时：10 leftTicket 12 train_location 15
         *
         * 商务特等座：32 一等座：31 二等座：30 高级软卧：21 软卧：23 动卧：33 硬卧：28 软座：24 硬座：29 无座：26
         * 其他：22 备注：1
         *
         * start_train_date:车票出发日期：13
         *
         * secretStr：0
         */

        for (String string : checi) {
            Map<String, String> result = new ConcurrentHashMap<String, String>();
            String[] ent = string.split("\\|");

            String secret = ent[0];
            secret = URLDecoder.decode(secret);// 解码
            String chehao = ent[3];
            result.put("二等座", ent[30]);
            result.put("一等座", ent[31]);
            result.put("硬卧", ent[28]);
            result.put("硬座", ent[29]);
            result.put("商务座", ent[32]);
            result.put("特等座", ent[32]);
            result.put("高级软卧", ent[21]);
            result.put("软卧", ent[23]);
            result.put("软座", ent[28]);
            result.put("无座", ent[29]);

            result.put("secret", secret);
            result.put("leftTicket", ent[12]);
            result.put("train_no", ent[2]);
            result.put("fromStationTelecode", ent[6]);
            result.put("toStationTelecode", ent[7]);
            result.put("train_location", ent[15]);
            result.put("chehao", chehao);
            result.put("start_train_date", ent[13]);
            map.put(chehao, result);

        }
    }

    /**
     * 预定车票
     */
    public void reserveTicket(String seat) {
        try {
            System.out.println("预定下单");
            String orderId = "";
            Map<String, String> map = null;
            while (orderId.equals("") && (map = Constant.bookQueue.take()) != null) {
                // while (orderId.equals("")) {
                //校验登录
                System.out.println("校验登录结果" + LoginService.checkOnline());
                //
                //点击预定
                if (!submitOrderRequest(map.get("secret"))) {
                    System.out.println("点击预定按钮失败");
                    return;
                }
                //获取token
                String token = initDc();
                if ("".equals(token)) {
                    return;
                }
                String key_check_isChange = token.split(",")[1];
                token = token.split(",")[0];
                //就是 死循环的购票
                String result = "B";
                while (true) {
                    //下单
                    result = placeOrder(token, seat);
                    if (result.equals("X"))
                        continue;
                    if (result.equals("Y"))
                        checkedCode();  //识别验证码
                    break;
                }
                //先进入小黑屋一段时间更有利于 抢票哦。
                if (!getQueueCount(token, map)) {
                    Constant.blacklist.put(map.get("chehao") + "_" + map.get("toBuySeat"), DateUtil.getDate("yyyyMMddHHmmss"));
                    System.out.println("当前车次, 排队人数已经超过余票张数，加入小黑屋！");
                    Constant.run = true;
                    return;
                }
                // 确认订单信息
                confirmSingle(token, key_check_isChange, map.get("toBuySeat"), map);
                //订单排队等待
                orderId = waitOrder(token);
                if (StringUtils.isBlank(orderId)) { //脸黑,人品差，长得磕碜 导致抢票失败
                    Constant.run = true;
                    return;
                } else {//啦啦啦 啦啦啦  抢票成功！ 发送 邮件 通知
                    EmailService emailService = SpringContextUtil.getBean(EmailService.class);
                    emailService.sendMessageMail(ticketConfig.getEmailInfo());
                    System.out.println("你已经抢票成功，欢迎下次继续使用半仙抢票系统!");
                    System.out.println("线程" + Thread.currentThread().getId() + "抢票结束!");
                    System.exit(0);
                    return;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean submitOrderRequest(String secretStr) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        map.put("back_train_date", shortSdf.format(cal.getTime()));
        map.put("purpose_codes", "ADULT");
        map.put("query_from_station_name", StationService.getCode(ticketConfig.getDeparture()));
        map.put("query_to_station_name", StationService.getCode(ticketConfig.getArrival()));
        map.put("secretStr", secretStr);
        map.put("train_date", ticketConfig.getDate());
        map.put("tour_flag", "dc");
        map.put("undefined", "");

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", ApiUrl.userAgent);
        headers.put("Host", ApiUrl.host);
        headers.put("Referer", "https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc");
        headers.put("Origin", ApiUrl.baseUrl);
        headers.put("X-Requested-With", "XMLHttpRequest");
        String result = "";
        if (httpProxy == null) {
            result = HttpClientTool.doPost(ApiUrl.submitOrderRequest, null, map);
        } else {
            result = HttpClientTool.doPost(ApiUrl.submitOrderRequest, null, map);
        }
        if ("302".equals(result)) return false;
        Map rsmap = JSON.parseObject(result, Map.class);
        assert rsmap != null;
        return null != rsmap.get("status") && rsmap.get("status").toString().equals("true");
    }

    /**
     * 校验验证码
     */
    public void checkedCode() {
        int i = 0;
        //二十次机会 自动识别验证码      不要问半仙为啥 20 次  我随便写的
        while (i < 20) {
            i++;
            try {
                // 获取验证码
                String captchaImage = Util.getCheckImg();
                String position = OrcUtil.getImgPositionBy360(captchaImage);
                //使用360验证码识别打码失败，启用AI验证码识别！
                if (StringUtils.isBlank(position)) {
                    position = OrcUtil.getImgPositionByAi(captchaImage);
                }
                if (StringUtils.isBlank(position)) {
                    System.out.println("完犊子了,打码失败!");
                    return;
                }
                Map<String, String> map = new HashMap();
                String result = HttpClientTool.doPost(ApiUrl.checkRandCodeAnsyn, headers, map);
                Map<String, Object> rsmap = JSON.parseObject(result, Map.class);
                if ((rsmap.get("status") + "").equalsIgnoreCase("true")) {
                    Map<String, Object> dataMap = (Map<String, Object>) rsmap.get("data");
                    String msg = rsmap.get("msg") + "";
                    if (msg.equalsIgnoreCase("TRUE")) {
                        System.out.println("验证码校验通过");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 下单
     */
    public String placeOrder(String token, String seat) {
        try {
            String result = HttpClientTool.doPost(ApiUrl.checkOrderInfo, headers, buyTicketsParams(token, seat));
            Map<String, Object> rsmap = JSON.parseObject(result, Map.class);
            if (rsmap.get("status") != null && rsmap.get("status").toString().equalsIgnoreCase("true")) {
                Map<String, Object> dataMap = (Map<String, Object>) rsmap.get("data");
                String ifShowPassCode = dataMap.get("ifShowPassCode") + "";// 是否需要验证码 Y需要  N不需要
                String ifShowPassCodeTime = dataMap.get("ifShowPassCodeTime") + "";// 不知道是否要等待这么久2801
                //等待安全期  （半仙也不知道是不是这样做的）
                Thread.sleep(Integer.parseInt(ifShowPassCodeTime));
                if (ifShowPassCode.equals("Y")) {
                    // 验证码
                    return "Y";
                } else {
                    return "N";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N";
    }

    /**
     * 生成订单时需要先请求这个页面
     *
     * @return result
     */
    private String initDc() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("_json_att", "");
        String result = HttpClientTool.doGetSSL(ApiUrl.initDc, null, null);
        if ("302".equals(result)) {
            System.out.println("token获取失败");
        }
        String token = "";
        String regex = "globalRepeatSubmitToken \\= '(.*?)';";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(result);
        while (m.find()) {
            token = m.group(1);
        }
        regex = "'key_check_isChange':'(.*?)',";
        Pattern p1 = Pattern.compile(regex);
        Matcher m1 = p1.matcher(result);
        while (m1.find()) {
            token += "," + m1.group(1);
        }
        return token;
    }

    /**
     * 头部
     */
    public void initHeards() {
        headers = new HashMap<>();
        headers.put("User-Agent", ApiUrl.userAgent);
        headers.put("Host", ApiUrl.host);
        headers.put("Referer", "https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc");
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Origin", ApiUrl.baseUrl);
    }

    /**
     * 获取乘客信息列表
     */
    public List<Map<String, String>> initPassengerInfo(String token) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("REPEAT_SUBMIT_TOKEN", token);
        map.put("_json_att", "");
        List<Map<String, String>> users = new ArrayList<>();
        String result = HttpClientTool.doPost(ApiUrl.getPassengerDTOs, headers, map);
        Map<String, Object> rsmap = JSON.parseObject(result, Map.class);
        {
            if (rsmap.get("status").toString().equalsIgnoreCase("true")) {
                Map<String, Object> dataMap = (Map<String, Object>) rsmap.get("data");
                users = (List<Map<String, String>>) dataMap.get("normal_passengers");
            }
        }
        return users;
    }

    /**
     * 提交车票信息所需参数
     *
     * @param token token
     * @param seat  车票类型
     * @return
     * @throws Exception
     */
    public Map<String, String> buyTicketsParams(String token, String seat) throws Exception {
        String[] userStr = ticketConfig.getUserNames().split(",");
        List<Map<String, String>> userList = initPassengerInfo(token);
        // 姓名，证件类别，证件号码，用户类型
        String oldPassengerStr = "";
        // 座位类型，0，车票类型，姓名，身份正号，电话，N（多个的话，以逗号分隔）
        String passengerTicketStr = "";

        //动态拼接订票座位信息
        for (Map<String, String> users : userList) {
            for (String str : userStr) {
                if (str.equals(users.get("passenger_name"))) {
                    oldPassengerStr += users.get("passenger_name") + "," + users.get("passenger_id_type_code") + ","
                            + users.get("passenger_id_no") + "," + users.get("passenger_type") + "_";
                    passengerTicketStr += SeatType.getSeat(seat) + ",0,1," + users.get("passenger_name") + ","
                            + users.get("passenger_id_type_code") + "," + users.get("passenger_id_no") + ","
                            + users.get("mobile_no") + ",N_";
                }
            }
        }
        passengerTicketStr = passengerTicketStr.endsWith("_")
                ? passengerTicketStr.substring(0, passengerTicketStr.length() - 1) : passengerTicketStr;
        this.oldPassengerStr = oldPassengerStr;
        this.passengerTicketStr = passengerTicketStr;
        //whatsSelect 1 成人票 0：学生票 tour_flag dc 单程
        Map<String, String> map = new HashMap<>();
        map.put("bed_level_order_num", "000000000000000000000000000000");
        map.put("cancel_flag", "2");
        map.put("oldPassengerStr", oldPassengerStr);
        map.put("passengerTicketStr", passengerTicketStr);
        map.put("REPEAT_SUBMIT_TOKEN", token);
        map.put("tour_flag", "dc");
        map.put("whatsSelect", "1");
        map.put("_json_att", "");
        return map;
    }


    /**
     * 获取排队和余票信息
     *
     * @param token
     * @param map
     * @return 余票不够时的提示信息，空表示余票够
     */
    public Boolean getQueueCount(String token, Map<String, String> map) {


        try {
            Map<String, String> params = new HashMap<>();
            map.put("fromStationTelecode", "fromStationTelecode");
            map.put("toStationTelecode", "toStationTelecode");
            map.put("leftTicket", map.get("leftTicket"));
            map.put("purpose_codes", "00");
            map.put("REPEAT_SUBMIT_TOKEN", token);
            map.put("seatType", SeatType.getSeat(map.get("toBuySeat")));
            map.put("stationTrainCode", map.get("chehao"));
            map.put("train_date", map.get("train_location"));// 时间格式待定 Sun+Feb+25+2018+00:00:00+GMT+0800
            map.put("train_no", map.get("train_no"));
            map.put("_json_att", "");
            String result = HttpClientTool.doPost(ApiUrl.getQueueCount, headers, map);
            Map rsmap = JSON.parseObject(result, Map.class);
            if (rsmap.get("status").toString().equals("true")) {
                JSONObject data = JSON.parseObject(rsmap.get("data").toString());
                return !data.getBooleanValue("op_2");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 排队等待订单
     *
     * @param token
     */
    public String waitOrder(String token) {
        int waitTime = 0;
        String orderId = "";
        String message = "";
        try {
            while (waitTime >= 0) {
                String result = HttpClientTool.doGet(String.format(
                        ApiUrl.queryOrderWaitTime + "?random=%s&tourFlag=dc&_json_att=&REPEAT_SUBMIT_TOKEN=",
                        System.currentTimeMillis(), token), headers, null);
                Map<String, Object> rsmap = JSON.parseObject(result, Map.class);
                if (rsmap.get("status").toString().equals("true")) {
                    Map<String, Object> data = (Map<String, Object>) rsmap.get("data");
                    waitTime = Integer.parseInt(data.get("waitTime").toString());
                    String waitCount = data.get("waitCount") + "";
                    orderId = String.valueOf(data.get("orderId"));

                    System.out.println(("前面" + waitCount + "人，需等待：" + waitTime + ""));
                    message = data.get("msg") + "";
                    if (null != data.get("msg")) {// 已有订单
                        System.out.println(data.get("msg"));
                        System.out.println("线程" + Thread.currentThread().getId() + "抢票结束!");
                        System.exit(0);
                    }
                    Thread.sleep(1000);
                }
            }
            if (orderId.equals("")) {
                System.out.println("获取订单号失败：" + message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return orderId;
    }

    /**
     * 确认订单成功!
     *
     * @param token              token
     * @param key_check_isChange key
     * @param seat               seat
     * @param map                map
     */
    public void confirmSingle(String token, String key_check_isChange, String seat, Map<String, String> map) {

        try {
            //whatsSelect 1 成人票 0：学生票 tour_flag dc 单程
            Map<String, String> param = new HashMap<>();
            param.put("choose_seats", "");
            param.put("dwAll", "N");
            param.put("key_check_isChange", key_check_isChange);
            param.put("leftTicketStr", map.get("leftTicket"));
            param.put("oldPassengerStr", oldPassengerStr);
            param.put("passengerTicketStr", passengerTicketStr);
            param.put("purpose_codes", "00");
            param.put("randCode", "");
            param.put("REPEAT_SUBMIT_TOKEN", token);
            param.put("roomType", "00");
            param.put("seatDetailType", "000");
            param.put("train_location", map.get("train_location"));
            param.put("whatsSelect", "1");
            param.put("_json_att", "");

            String result = HttpClientTool.doPost(ApiUrl.confirmSingleForQueue, headers, map);
            Map<String, Object> rsmap = JSON.parseObject(result, Map.class);
            if (rsmap.get("status").toString().equals("true")) {
                Map<String, Object> data = (Map<String, Object>) rsmap.get("data");
                String subStatus = data.get("submitStatus") + "";// true为成功
                // false为失败
                if (subStatus.equals("true")) {
                    System.out.println(" 确认订单成功！");
                } else {
                    System.out.println("失败原因" + data.get("errMsg"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("订单提交异常啦!");
        }
    }
}
