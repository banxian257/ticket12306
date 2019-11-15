package com.wza.module.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StationVo implements Serializable {
    //名称
    private String mame;
    //编号
    private String code;
    //拼音
    private String pinyin;
    //首字母
    private String firstPinyin;
    //首字母
    private String capitalization;
    //乱拼
    private String chaosPinyin;
}
