package com.wza.module.service;

import com.wza.common.util.HttpClientTool;
import com.wza.common.util.ProxyCache;
import com.wza.common.util.Util;
import com.wza.module.entity.HttpProxy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


@Component
public class     GetProxyTask {

    private Logger logger = LoggerFactory.getLogger(GetProxyTask.class);


    public static void main(String[] args) {
/*        GetProxyTask getProxyTask = new GetProxyTask();
        getProxyTask.getProxyIp();
        for (HttpProxy httpProxy : ProxyCache.getProxy()) {
            System.out.println(httpProxy.getIp() + "      " + httpProxy.getPort());
        }*/


    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void getProxyIp() {
        try {
            for (int page = 1; page < 50; page++) {
                this.getProxyIp3366(page);
            }
        } catch (Exception e) {
            logger.error("获取ip3366出错", e);
        }
    }

    @Async("proxyIpPool")
    public void getProxyIp3366(int page) throws Exception {
        String baseUrl = "http://www.ip3366.net/free/?stype=1&page=";
        String url = baseUrl + page;
        HttpGet httpget = new HttpGet(url);
        CloseableHttpClient httpclient = HttpClientTool.createSSLClientDefault();
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String html = HttpClientTool.getResponseString(response);
            Document document = Jsoup.parse(html);
            Element tbody = document.getElementById("list").getElementsByTag("tbody").first();
            Elements tr = tbody.getElementsByTag("tr");
            for (Element entry : tr) {
                Elements tds = entry.getElementsByTag("td");
                String ip = tds.get(0).text();
                String port = tds.get(1).text();
                HttpProxy httpProxy = new HttpProxy();
                httpProxy.setIp(ip);
                httpProxy.setPort(Util.getInt(port));
                ProxyCache.addProxy(httpProxy);
            }
        }
    }
}
