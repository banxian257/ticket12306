package com.wza.common.config;

import com.wza.common.util.SpringContextUtil;
import com.wza.module.service.GetProxyTask;
import com.wza.module.service.LoginService;
import com.wza.module.service.QueryTicket;
import com.wza.module.service.StationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LoginRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化车站信息
        StationService.init();
        GetProxyTask getProxyTask = SpringContextUtil.getBean(GetProxyTask.class);
        //初始化代理ip
        getProxyTask.getProxyIp();
        LoginService loginService = SpringContextUtil.getBean(LoginService.class);
        //初始化查询余票 地址
        QueryTicket.init();
        //登录
        loginService.login();
    }
}
