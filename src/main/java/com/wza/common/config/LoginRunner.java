package com.wza.common.config;

import com.wza.common.util.SpringContextUtil;
import com.wza.module.service.LoginService;
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
        LoginService loginService = SpringContextUtil.getBean(LoginService.class);
        //登录
        loginService.login();
    }
}
