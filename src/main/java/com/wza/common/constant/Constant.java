package com.wza.common.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Constant {
    public static final String StationKey = "STATIONREDISKEYTICKET";

    //黑名单
    public static Map<String, String> blacklist = new HashMap<>();
    //是否执行
    public static boolean run = false;
    //是否执行
    public static boolean suc = false;
    public static final BlockingQueue<Map<String, String>> bookQueue = new LinkedBlockingQueue<Map<String, String>>(10);
}
