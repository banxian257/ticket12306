package com.wza.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableSwagger2
public class Swagger {
    @Bean
    public Docket apiManage() {
        // [ Base URL: xxx.xxx.cn/ ] https://xxx.xxx.cn/v2/api-docs?group=SplashAPI
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("WZA后台API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wza.module"))
                .paths(PathSelectors.ant("/api/v?/client/**"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                // .globalOperationParameters(pars)
                .apiInfo(apiInfo());
    }

    private List<ApiKey> securitySchemes() {
        return newArrayList(
                new ApiKey("Authorization", "Authorization", "header"));
    }

    private List<SecurityContext> securityContexts() {
        return newArrayList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build()
        );
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(
                new SecurityReference("Authorization", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "WZA API",
                "API文档，有问题请联系我们 - WZA研发团队\n " +
                        "需要认证的API，不提供Authorization头、提供错误、无效、过期的返回401",
                "API TOS",
                "Terms of service",
                new Contact("WZA研发中心", "", ""),
                "License of API", "API license URL", Collections.emptyList());
    }
}
