package com.wza.module.service;

import lombok.Data;

import java.util.Map;

@Data
public class EmailInfo {
    // 接收方地址 多个用 ; 隔开
    private String toAddress;
    // 抄送邮件地址 多个用 ; 隔开
    private String copyToAddress;
    //主题
    private String subject;

    // 动态参数值  k-> 参数  v-> 值
    private Map<String, Object> params;

    //vm 模板名称
    private String templateName;

    //附件信息 存路径
    private String[] fileNames;
}
