package com.wza.common.util;


import com.wza.module.entity.HttpProxy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ProxyCache
{

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



    public static Set<HttpProxy> getProxy()
    {
        return proxyList;
    }

    public static void addProxy(HttpProxy httpProxy)
    {
        if (proxyList.size() == MAX_SIZE)
        {
            proxyList.remove(0);
        }
        proxyList.add(httpProxy);
    }


    public static void setUsableProxy(List<HttpProxy> proxy)
    {
        usableProxy.addAll(proxy);

    }
}
