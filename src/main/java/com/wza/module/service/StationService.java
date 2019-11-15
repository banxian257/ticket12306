package com.wza.module.service;

import com.wza.common.util.HttpClient;
import com.wza.module.vo.StationVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StationService {

    @Value("${city_url}")
    private String url;

    /**
     * 获取全国城市 车站
     *
     * @return 车站信息
     */
    public List<StationVo> listStation() {
        List<StationVo> list = new ArrayList<>();
        //获取车站信息 并处理
        String stationStr = HttpClient.get(url);
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
        });
        return list;
    }
}
