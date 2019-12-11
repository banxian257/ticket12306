package com.wza.module.entity;


import lombok.Data;

@Data
public class HttpProxy
{
    private String ip;
    private Integer port;

    private String username;
    private String password;
}
