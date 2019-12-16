package com.wza.module.service;

import com.wza.common.util.ApiUrl;
import com.wza.common.util.HttpClient;
import com.wza.common.util.HttpClientTool;
import com.wza.module.vo.StationVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StationService {

    //@Value("${city_url}")
    private String url;

/*    *//**
     * 获取全国城市 车站
     *
     * @return 车站信息
     *//*
    public List<StationVo> listStation() {
        List<StationVo> list = new ArrayList<>();
        //获取车站信息 并处理
*//*        String stationStr = HttpClientTool.doGet(ApiUrl.,null,null);
        stationStr = stationStr.replaceFirst("var station_names ='@", "").replaceFirst("';", "");
        Arrays.asList(stationStr.split("@")).stream().forEach(str -> {
            StationVo cityVo = new StationVo();
            String[] citys = str.split("\\|");
            cityVo.setChaosPinyin(citys[0]);
            cityVo.setMame(citys[1]);
            cityVo.setCapitalization(citys[2]);
            cityVo.setPinyin(citys[3]);
            cityVo.setFirstPinyin(citys[4]);
            cityVo.setCode(citys[5]);
            list.add(cityVo);
        });*//*
        return list;
    }*/

    public static Map<String, String> code = new HashMap<>();

    public static Map<String, String> name = new HashMap<>();

    public static String getCode(String name) {
        return code.get(name);

    }

    public static String getName(String code) {
        return name.get(code);

    }

    /**
     * 初始化车站信息
     */
    public static void init() {
        //获取车站信息 并处理
        String result = HttpClient.sendGet(ApiUrl.station);
        String[] s = result.substring(result.indexOf("'") + 1).split("@");
        //创建二维数组 接收车站信息
        String[][] stations = new String[s.length - 1][2];
        //第一个是空排除
        for (int i = 1; i < s.length; i++) {
            String[] str = s[i].split("[|]");
            stations[i - 1][0] = str[1];
            stations[i - 1][1] = str[2];
        }
        for (String[] str : stations) {
            code.put(str[0], str[1]);
            code.put(str[1], str[0]);
        }
    }

    public static void main(String[] args) {
        init();
        System.out.println( getCode(""));
    }


}
