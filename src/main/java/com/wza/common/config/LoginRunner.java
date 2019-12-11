package com.wza.common.config;

import com.wza.common.util.SpringContextUtil;
import com.wza.module.service.LoginService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LoginRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        LoginService loginService= SpringContextUtil.getBean(LoginService.class);
        loginService.login();
    }
}
