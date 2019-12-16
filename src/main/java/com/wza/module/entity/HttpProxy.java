package com.wza.module.entity;


import lombok.Data;

@Data
public class HttpProxy {
    //ip
    private String ip;
    //端口
    private Integer port;

    private String username;
    private String password;
}
