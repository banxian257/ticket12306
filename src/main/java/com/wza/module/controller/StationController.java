package com.wza.module.controller;

import com.wza.common.util.Result;
import com.wza.module.controller.birdge.StationBridge;
import com.wza.module.service.StationService;
import com.wza.module.vo.StationVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("search/")
public class StationController {
    @Resource
    private StationService stationService;
    @Resource
    private StationBridge  stationBridge;

    @RequestMapping("city")
    public Result SearchStation(@RequestParam String name) {
        List<StationVo> list =  stationBridge.listStation(name);
        return Result.buildSucc(list);
    }
}
