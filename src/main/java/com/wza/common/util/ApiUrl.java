package com.wza.common.util;

/**
 * 12306 相关Url
 */
public interface ApiUrl {
    String baseUrl = "https://kyfw.12306.cn";

    String leftTicketByCdn = "http://%s/otn/%s?leftTicketDTO.train_date=%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=ADULT";

    String leftTicket = baseUrl
            + "/otn/leftTicket/query?leftTicketDTO.train_date=%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=ADULT";

    String login = baseUrl + "/passport/web/login";

    String passport=baseUrl+"/otn/passport?redirect=/otn/login/userLogin";

    String userLogin = baseUrl + "/otn/login/userLogin";

    String conf = baseUrl + "/otn/login/conf";

    String captchaImage = baseUrl + "/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&_=%s";

    String captchaCheck = baseUrl + "/passport/captcha/captcha-check?answer=%s&rand=sjrand&login_site=E&_=%s";

    String uamtk = baseUrl + "/passport/web/auth/uamtk";

    String uamauthclient = baseUrl + "/otn/uamauthclient";

    String uamtkStatic = baseUrl + "/passport/web/auth/uamtk-static";

    String userInfo = baseUrl + "/otn/modifyUser/initQueryUserInfoApi";

    String getPassengerDTOs = baseUrl + "/otn/confirmPassenger/getPassengerDTOs";

    String submitOrderRequest = baseUrl + "/otn/leftTicket/submitOrderRequest";

    String checkOrderInfo = baseUrl + "/otn/confirmPassenger/checkOrderInfo";

    String initDc = baseUrl + "/otn/confirmPassenger/initDc"; // 生成订单时需要先请求这个页面

    String getQueueCount = baseUrl + "/otn/confirmPassenger/getQueueCount";

    String confirmSingleForQueue = baseUrl + "/otn/confirmPassenger/confirmSingleForQueue";

    String queryOrderWaitTime = baseUrl + "/otn/confirmPassenger/queryOrderWaitTime?%s";

    String queryInitPage = baseUrl + "/otn/leftTicket/init";

    String loginInitPage = baseUrl + "/otn/login/init";

    String browserDeviceId = baseUrl + "/otn/HttpZF/logdevice";

    String initMy12306 = baseUrl + "/otn/index/initMy12306";

    String loginAysnSuggest = baseUrl + "/otn/login/loginAysnSuggest";

    String autoSubmitOrderRequest = baseUrl + "/otn/confirmPassenger/autoSubmitOrderRequest";

    // 360验证码识别
    String getCheck = "https://12306.jiedanba.cn/api/v2/getCheck";

    String getPositionBy360 = "http://check.huochepiao.360.cn/img_vcode";

    // 验证码识别 机器学习
    String getPositionByAi = "https://12306.jiedanba.cn/api/v2/img_vcode";

    String referer = baseUrl + "/otn/index/init";
    String resources = "https://kyfw.12306.cn/otn/resources/login.html";
    String host = "kyfw.12306.cn";
    String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    //自动识别登录验证码
    String automaticRecognition = "https://12306-ocr.pjialin.com/check";

    String station = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9098";
    String dc="https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc";

    static String getRailUrl = "https://kyfw.12306.cn/otn/HttpZF/logdevice" +
            //"?algID=8iIPwpB28l"+
            "?algID=XWptt1kXbs" +
            //"&hashCode=XQTW18RSf_dOYnqpGaj_yA-WKSsKbH77QE7zN-cCdB0"+
            "&hashCode=K7H-SogpeC4-tIYLqCXJabQWEnBXNROVnaPuQXU8Vec" +
            "&FMQw=0" +
            "&q4f3=zh-CN" +
            "&VPIf=1" +
            "&custID=133" +
            "&VEek=unknown" +
            "&dzuS=0" +
            "&yD16=0" +
            "&EOQP=89f60554e6cb588cf7dcc391a91488a1" +
            "&lEnu=176525634" +
            "&jp76=52d67b2a5aa5e031084733d5006cc664" +
            "&hAqN=Win32" +
            "&platform=WEB" +
            "&ks0Q=d22ca0b81584fbea62237b14bd04c866" +
            "&TeRS=1010x1680" +
            "&tOHY=24xx1050x1680" +
            "&Fvje=i1l1o1s1" +
            "&q5aJ=-8" +
            "&wNLf=99115dfb07133750ba677d055874de87" +
            "&0aew=Mozilla/5.0%20(Windows%20NT%2010.0;%20Win64;%20x64)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Chrome/76.0.3809.100%20Safari/537.36" +
            "&E3gR=a54e6d6499e595ae2cfaf80eda685cc2" +
            "&timestamp=" + String.valueOf(System.currentTimeMillis());
}
