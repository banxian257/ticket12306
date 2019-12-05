package com.wza.module.controller;

import com.wza.common.util.Result;
import com.wza.module.dto.LoginDto;
import com.wza.module.entity.TicketConfig;
import com.wza.module.service.BuyTickets;
import com.wza.module.service.LoginService;
import com.wza.module.service.StationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("ticket")
public class LoginController {
    @Resource
    private LoginService loginService;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result SearchStation(@RequestBody LoginDto login) {

        if (StringUtils.isBlank(login.getName()) || StringUtils.isBlank(login.getPwd()))
            return Result.buildFail("账号或密码不能为空");
        loginService.login(login.getName(),login.getPwd());
        return Result.buildSucc(null);
    }

    @RequestMapping(value = "/aa", method = RequestMethod.GET)
    public Result SearchStation() {

        TicketConfig t=new TicketConfig();
        StationService.init();
        t.setDate("2019-11-25");
        t.setArrival(StationService.getCode("徐州东"));
        t.setDeparture(StationService.getCode("上海虹桥"));
        BuyTickets b=new BuyTickets();

        b.QueryTicket(t);
        return Result.buildSucc(null);
    }
}
