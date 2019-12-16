package com.wza.common.util;

import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import com.wza.module.entity.HttpProxy;
import com.wza.module.entity.Logdevice;
import org.apache.http.client.methods.HttpGet;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于获取cookies
 */
public class LogdeviceUtil {

    private static String cookieUrl;

    public static Logdevice getLogdevice() {

    /*    Config config = SpringContextUtil.getBean(Config.class);
        CookiesConfig cookiesConfig = SpringContextUtil.getBean(CookiesConfig.class);
        if(cookiesConfig.getEnable()){
            return new Logdevice(cookiesConfig.getRailExpiration(),
                    cookiesConfig.getRailDeviceid());
        }
        String proxyHost = config.getEnableProxy() ? config.getProxyIp().getIp() : null;
        int proxyPort = config.getEnableProxy() ? config.getProxyIp().getPort() : 0;*/
        HttpProxy httpProxy=ProxyCache.getHttpProxy();
        try {
            String url = LogdeviceUtil.getLogdeviceUrl(httpProxy.getIp(), httpProxy.getPort());
            if (StringUtils.isEmpty(url)) {
                System.out.println("获取cookieurl出错，稍后再试");
                return null;
            }
         String msg=   HttpClientTool.doGetSSL(url,null,null);
            msg = msg.replace("callbackFunction('", "");
            msg = msg.replace("')", "");

            Map obj =  (Map) JSON.parseObject(msg, Map.class);
            Logdevice model = new Logdevice(obj.get("exp").toString(),
                    obj.get("dfp").toString());
            return model;
        } catch (Exception e) {
            System.out.println("获取cookie出错，稍后再试");
        }
        return null;
    }

    public static String getLogdeviceUrl(String proxyHost, int proxyPort) {
        WebClient wc = new WebClient(BrowserVersion.CHROME, proxyHost, proxyPort);
        wc.getOptions().setTimeout(15000);
        wc.getOptions().setUseInsecureSSL(true);
        wc.getOptions().setJavaScriptEnabled(true);
        wc.getOptions().setCssEnabled(false);
        //当JS执行出错的时候是否抛出异常, 这里选择不需要
        wc.getOptions().setThrowExceptionOnScriptError(true);
        //当HTTP的状态非200时是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(true);
        //很重要，设置支持AJAX
        wc.setAjaxController(new AjaxController() {
            @Override
            public boolean processSynchron(HtmlPage page, WebRequest settings, boolean async) {
                return super.processSynchron(page, settings, async);
            }
        });
        wc.setWebConnection(
                new WebConnectionWrapper(wc) {
                    @Override
                    public WebResponse getResponse(WebRequest request) throws IOException {
                        WebResponse response = super.getResponse(request);

                        if (request.getUrl().toExternalForm().contains("/otn/HttpZF/logdevice")) {
                            cookieUrl = request.getUrl().toExternalForm();
                        }
                        return response;

                    }
                }

        );
        try {
            wc.getPage("https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            wc.waitForBackgroundJavaScript(3 * 1000);
        }
        while (StringUtils.isEmpty(cookieUrl)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cookieUrl;
    }
}
