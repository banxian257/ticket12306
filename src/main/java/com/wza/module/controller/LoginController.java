package com.wza.module.controller;

import com.wza.common.util.Result;
import com.wza.module.dto.LoginDto;
import com.wza.module.service.LoginService;
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
}
