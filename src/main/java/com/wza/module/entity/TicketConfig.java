package com.wza.module.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TicketConfig {
    //出发地
    private String departure;
    //到达地
    private String arrival;
    //日期
    private String date;
    //车次
    private List<String> ticket;
    //乘客信息
    private List<Map<String, String>> users;
}
