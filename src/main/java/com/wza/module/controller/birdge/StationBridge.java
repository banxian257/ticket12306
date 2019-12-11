package com.wza.module.controller.birdge;

import com.wza.common.constant.Constant;
import com.wza.module.service.StationService;
import com.wza.module.vo.StationVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author  banxian
 * @date  2019/11/25
 */
@Component
public class StationBridge {
    @Resource
    private RedisTemplate<String, List<StationVo>> redisTemplate;
    @Resource
    private StationService  stationService;

    /**
     * 获取 全国车站信息
     *
     * @return 返回城市集合
     */
    public List<StationVo> listStation(String name) {
        List<StationVo> list= new ArrayList<>();

/*        //获取车站信息
        if (redisTemplate.opsForValue().get(Constant.StationKey) == null) {
           // list =  stationService.listStation();
           // redisTemplate.opsForValue().set(Constant.StationKey, list, 1, TimeUnit.DAYS);

        } else {
            list = redisTemplate.opsForValue().get(Constant.StationKey);
        }

        //是否带条件检索
        if (!StringUtils.isEmpty(name)) return listStationWhere(list, name);*/

        return list;
    }

    private List<StationVo> listStationWhere(List<StationVo> list, String name) {
        List<StationVo> stations = new ArrayList<>();
  /*      long a=System.currentTimeMillis();
        list.stream().forEach( stationVo -> {
            if (stationVo.getMame().contains(name) || stationVo.getPinyin().contains(name)
                    || stationVo.getChaosPinyin().contains(name) || stationVo.getFirstPinyin().contains(name) || stationVo.getCapitalization().contains(name)
            ) {
                stations.add(stationVo);
            }
        });
        System.out.println(System.currentTimeMillis()-a);*/
        return stations;
    }
}
