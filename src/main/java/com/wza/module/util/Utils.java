package com.wza.module.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wza.common.util.HttpClientTool;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Utils {

    static CookieStore cookieStore = new BasicCookieStore();
    static CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

    static String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    static String acceptEncoding = "gzip, deflate, br";
    static String acceptLanguage = "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2";
    static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0";

    //url
    static String getStationUrl = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js";
    static String queryTicketsUrl = "https://kyfw.12306.cn/otn/leftTicket/query?";
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
    static String initUrl = "https://kyfw.12306.cn/otn/login/init";
    static String loginUrl = "https://kyfw.12306.cn/passport/web/login";
    static String logoutUrl = "https://kyfw.12306.cn/otn/login/conf";
    static String logoutUamtk = "https://kyfw.12306.cn/passport/web/auth/uamtk-static";
    static String getYzmUrl = "https://kyfw.12306.cn/passport/captcha/captcha-image64?";
    static String checkYzmUrl = "https://kyfw.12306.cn/passport/captcha/captcha-check?";
    static String getUamTk = "https://kyfw.12306.cn/passport/web/auth/uamtk";
    static String uamAuthClient = "https://kyfw.12306.cn/otn/uamauthclient";
    static String queryPassengersUrl = "https://kyfw.12306.cn/otn/passengers/query";

    static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    static CookieStore getCookieStore() {
        return cookieStore;
    }

    static void addRailCookies(String exp, String dfp) {
        BasicClientCookie expCookie = new BasicClientCookie("RAIL_EXPIRATION", exp);
        expCookie.setDomain("kyfw.12306.cn");
        expCookie.setPath("/");
        cookieStore.addCookie(expCookie);
        BasicClientCookie dfpCookie = new BasicClientCookie("RAIL_DEVICEID", dfp);
        dfpCookie.setDomain("kyfw.12306.cn");
        dfpCookie.setPath("/");
        cookieStore.addCookie(dfpCookie);
    }

    static void setGetRequestHeaders(HttpGet get) {
        get.addHeader("Accept", Utils.accept);
        get.addHeader("Accept-Encoding", Utils.acceptEncoding);
        get.addHeader("Accept-Language", Utils.acceptLanguage);
        get.addHeader("User-Agent", Utils.userAgent);
    }

    static void setPostRequestHeaders(HttpPost post) {
        post.addHeader("Accept", Utils.accept);
        post.addHeader("Accept-Encoding", Utils.acceptEncoding);
        post.addHeader("Accept-Language", Utils.acceptLanguage);
        post.addHeader("User-Agent", Utils.userAgent);
    }

    static void initLogin() throws ClientProtocolException, IOException {
        HttpGet getMethod = new HttpGet(Utils.initUrl);
        Utils.setGetRequestHeaders(getMethod);
        CloseableHttpResponse response = Utils.getHttpClient().execute(getMethod);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("初始化12306成功.");
        Utils.printCookies();
    }

    public static void main(String[] args) throws IOException {
        initRail();
    }
   public static void initRail() throws ClientProtocolException, IOException {
        //获取RAIL_DEVICEID和RAIL_EXPIRATION
        //String getJsUrl = "https://kyfw.12306.cn/otn/HttpZF/GetJS";
        HttpGet get = new HttpGet(Utils.getRailUrl);
        Utils.setGetRequestHeaders(get);
        CloseableHttpResponse response = Utils.getHttpClient().execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("GetJS成功.");
        Utils.printCookies();
        String resStr = EntityUtils.toString(response.getEntity());
        String str = resStr.substring(resStr.indexOf("{"), resStr.indexOf("}") + 1);
        System.out.println(str);
        JSONObject obj = JSON.parseObject(str);
        System.out.println(obj.getString("exp"));
        System.out.println(obj.getString("dfp"));
     //   Utils.addRailCookies(obj.getString("exp"), obj.getString("dfp"));
       BasicClientCookie expCookie = new BasicClientCookie("RAIL_EXPIRATION", obj.getString("exp"));
       expCookie.setDomain("kyfw.12306.cn");
       expCookie.setPath("/");
       HttpClientTool.cookieStore.addCookie(expCookie);
       BasicClientCookie dfpCookie = new BasicClientCookie("RAIL_DEVICEID", obj.getString("dfp"));
       dfpCookie.setDomain("kyfw.12306.cn");
       dfpCookie.setPath("/");
       HttpClientTool.cookieStore.addCookie(dfpCookie);
        obj.clear();

    }

    static void printCookies() {
        System.out.println("打印cookie信息：");
        List<org.apache.http.cookie.Cookie> cs = Utils.getCookieStore().getCookies();
        for (int i = 0; i < cs.size(); i++) {
            System.out.println(cs.get(i).getName() + ":" + cs.get(i).getValue());
        }
    }

    static void clearCookies() {
        System.out.println("清空cookie信息");
        cookieStore.clear();
    }

    static void putStation2Map(String result, HashMap<String, String> cityMap) {
        int startIdx = result.indexOf("'");
        int endIdx = result.length();
        String city = result.substring(startIdx, endIdx);
        String[] c = city.split("@");
        for (int i = 1; i < c.length; i++) {
            String[] cc = c[i].split("\\|");
            cityMap.put(cc[1], cc[2]);
        }
    }

    static String getKeyFromMap(HashMap<String, String> map, String value) {
        for (String getKey : map.keySet()) {
            if (map.get(getKey).equals(value)) {
                return getKey;
            }
        }
        return "";
    }

    static void setTableColumnWidth(Vector<String> tableHeaders, JTable table) {
        table.getColumn(tableHeaders.get(1)).setPreferredWidth(55);
        table.getColumn(tableHeaders.get(2)).setPreferredWidth(55);
        table.getColumn(tableHeaders.get(7)).setPreferredWidth(55);
        table.getColumn(tableHeaders.get(8)).setPreferredWidth(55);
        table.getColumn(tableHeaders.get(9)).setPreferredWidth(60);
        table.getColumn(tableHeaders.get(10)).setPreferredWidth(45);
        table.getColumn(tableHeaders.get(11)).setPreferredWidth(45);
        table.getColumn(tableHeaders.get(12)).setPreferredWidth(45);
        table.getColumn(tableHeaders.get(13)).setPreferredWidth(45);
        table.getColumn(tableHeaders.get(14)).setPreferredWidth(45);
        table.getColumn(tableHeaders.get(15)).setPreferredWidth(45);
        table.getColumn(tableHeaders.get(16)).setPreferredWidth(45);
    }

    static void setBackgoundColorForTable(JTable table) {
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                //return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JButton button;
                JPanel panel;
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    setBackground(Color.white); //设置奇数行底色
                } else if (row % 2 == 1) {
                    setBackground(new Color(238, 241, 248)); //设置偶数行底色
                }
                //设置'有'和'无'的颜色
                if (value != null && column >= 6 && column <= 16) {
                    if ("--".equals(value.toString()) || "无".equals(value.toString())) {
                        setForeground(new Color(153, 153, 153));
                    } else if ("有".equals(value.toString())) {
                        setForeground(new Color(38, 163, 6));
                    } else { //数字
                        setForeground(Color.black);
                    }
                } else {
                    setForeground(Color.black);
                }

                //添加预定按钮
                if (column == 17) {
                    Object obj6 = table.getModel().getValueAt(row, 6);
                    Object obj7 = table.getModel().getValueAt(row, 7);
                    Object obj8 = table.getModel().getValueAt(row, 8);
                    Object obj9 = table.getModel().getValueAt(row, 9);
                    Object obj10 = table.getModel().getValueAt(row, 10);
                    Object obj11 = table.getModel().getValueAt(row, 11);
                    Object obj12 = table.getModel().getValueAt(row, 12);
                    Object obj13 = table.getModel().getValueAt(row, 13);
                    Object obj14 = table.getModel().getValueAt(row, 14);
                    Object obj15 = table.getModel().getValueAt(row, 15);

                    if ((obj6 != null) && (!"--".equals(obj6.toString())) && (!"无".equals(obj6.toString())) &&
                            (("有".equals(obj6.toString())) || (Integer.parseInt(obj6.toString()) > 0)) ||
                            (obj7 != null) && (!"--".equals(obj7.toString())) && (!"无".equals(obj7.toString())) &&
                                    (("有".equals(obj7.toString())) || (Integer.parseInt(obj7.toString()) > 0)) ||
                            (obj8 != null) && (!"--".equals(obj8.toString())) && (!"无".equals(obj8.toString())) &&
                                    (("有".equals(obj8.toString())) || (Integer.parseInt(obj8.toString()) > 0)) ||
                            (obj9 != null) && (!"--".equals(obj9.toString())) && (!"无".equals(obj9.toString())) &&
                                    (("有".equals(obj9.toString())) || (Integer.parseInt(obj9.toString()) > 0)) ||
                            (obj10 != null) && (!"--".equals(obj10.toString())) && (!"无".equals(obj10.toString())) &&
                                    (("有".equals(obj10.toString())) || (Integer.parseInt(obj10.toString()) > 0)) ||
                            (obj11 != null) && (!"--".equals(obj11.toString())) && (!"无".equals(obj11.toString())) &&
                                    (("有".equals(obj11.toString())) || (Integer.parseInt(obj11.toString()) > 0)) ||
                            (obj12 != null) && (!"--".equals(obj12.toString())) && (!"无".equals(obj12.toString())) &&
                                    (("有".equals(obj12.toString())) || (Integer.parseInt(obj12.toString()) > 0)) ||
                            (obj13 != null) && (!"--".equals(obj13.toString())) && (!"无".equals(obj13.toString())) &&
                                    (("有".equals(obj13.toString())) || (Integer.parseInt(obj13.toString()) > 0)) ||
                            (obj14 != null) && (!"--".equals(obj14.toString())) && (!"无".equals(obj14.toString())) &&
                                    (("有".equals(obj14.toString())) || (Integer.parseInt(obj14.toString()) > 0)) ||
                            (obj15 != null) && (!"--".equals(obj15.toString())) && (!"无".equals(obj15.toString())) &&
                                    (("有".equals(obj15.toString())) || (Integer.parseInt(obj15.toString()) > 0))) {
                        panel = new JPanel();
                        button = new JButton();
                        button.setText("预定");
                        button.setForeground(Color.white);
                        button.setBackground(new Color(0, 119, 255));
                        panel.add(button, SwingConstants.CENTER);
                        return panel;
                    } else {
                        setForeground(new Color(153, 153, 153));
                        setValue("预定");
                    }
                }

                return cell;
            }
        };
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, tcr);
    }
}
