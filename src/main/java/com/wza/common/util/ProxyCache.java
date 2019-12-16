package com.wza.common.util;


import com.wza.module.entity.HttpProxy;

import java.util.*;


public class ProxyCache {

    /**
     * 最大爬去代理数
     */
    private static int MAX_SIZE = 1000;

    /**
     * 爬取到的代理
     */
    private static Set<HttpProxy> proxyList = new HashSet<>(MAX_SIZE);

    /**
     * 可用代理
     */
    private static List<HttpProxy> usableProxy = new ArrayList<>();


    public static Set<HttpProxy> getProxy() {
        return proxyList;
    }

    public static void delProxy(HttpProxy httpProxy){
        proxyList.remove(httpProxy);
    }

    public static void addProxy(HttpProxy httpProxy) {
        if (proxyList.size() >= MAX_SIZE) {
            proxyList.remove(0);
        }
        proxyList.add(httpProxy);
    }

    public static  void setUsableProxy(){
        usableProxy=new ArrayList<>(proxyList);
    }
    public static void setUsableProxy(List<HttpProxy> proxy) {
        usableProxy.addAll(proxy);

    }

    public static HttpProxy getHttpProxy() {
        if (usableProxy.size() <= 0) {
            return null;
        }
        Random r = new Random(0);
        int i = r.nextInt(usableProxy.size() - 1);
        return usableProxy.get(i);
    }

    public static void main(String[] args) {
        Random r = new Random(0);
        for (int j=0;j<100;j++){
            int i = r.nextInt(1000 - 1);
            System.out.println(i);
        }

    }
}
