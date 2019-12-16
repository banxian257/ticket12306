package com.wza.module.service;

import com.wza.common.constant.Constant;
import com.wza.common.util.ApiUrl;
import com.wza.common.util.HttpClientTool;
import com.wza.module.entity.HttpProxy;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.CookieStore;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryTicket {
    public static boolean checkIp(HttpProxy httpProxy) {
        try {
            CloseableHttpClient httpclient = HttpClientTool.getHttpClient(Constant.cookieStore);
            HttpGet httpget = new HttpGet("https://kyfw.12306.cn/otn/leftTicket/init");
            httpget.setHeader("Host", "kyfw.12306.cn");
            httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            httpget.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpget.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpget.setHeader("Referer", "https://www.12306.cn/index/");
            httpget.setHeader("linktypeid", "dc");
            HttpHost proxy = new HttpHost(httpProxy.getIp(), httpProxy.getPort());
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)
                    .setConnectionRequestTimeout(3000).setSocketTimeout(3000).setProxy(proxy).build();
            httpget.setConfig(requestConfig);
            HttpResponse response = httpclient.execute(httpget);
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (IOException e) {
        }

        System.out.println("无效ip：" + httpProxy.getIp()+"    "+httpProxy.getPort());
        return false;
    }

    public static void init() {
        Header[] headers = new BasicHeader[7];
        headers[0] = new BasicHeader("User-Agent", ApiUrl.userAgent);
        headers[1] = new BasicHeader("Host", ApiUrl.host);
        headers[2] = new BasicHeader("Referer", ApiUrl.loginInitPage);
        headers[3] = new BasicHeader("Accept", "*/*");
        headers[4] = new BasicHeader("Accept-Encoding", "gzip, deflate");
        headers[5] = new BasicHeader("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3");
        headers[6] = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
        String responseBody = "";
        try {
            HttpUriRequest confirm = RequestBuilder.post().setUri(new URI(ApiUrl.queryInitPage)).addHeader(headers[0])
                    .addHeader(headers[1]).addHeader(headers[2]).addHeader(headers[3]).addHeader(headers[4])
                    .addHeader(headers[5]).addHeader(headers[6]).addParameter("_json_att", "").build();
            CloseableHttpResponse response = HttpClientTool.getHttpClient(new BasicCookieStore()).execute(confirm);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity);

                Pattern p = Pattern.compile("CLeftTicketUrl \\= '(.*?)';");
                Matcher m = p.matcher(responseBody);
                while (m.find()) {
                    Constant.queryTicket= m.group(1);
                    System.out.println("初始化查询地址完成，地址是：[" +    Constant.queryTicket + "]");
                }
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
