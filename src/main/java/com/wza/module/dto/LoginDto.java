package com.wza.module.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginDto {
    @ApiModelProperty(name = "name", value = "12306账号")
    private String name;
    @ApiModelProperty(name = "pwd", value = "密码")
    private String pwd;

}
