package com.example;

import com.example.framework.common.exception.RestTemplateResponseErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;


import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
public class FrameworkManageApplication {
    private final ObjectMapper objectMapper;

    public FrameworkManageApplication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    @RequestScope
    public RestTemplate restTemplate(HttpServletRequest inReq, RestTemplateBuilder restTemplateBuilder) {
        final String authHeader =
                inReq.getHeader(HttpHeaders.AUTHORIZATION);
        final RestTemplate restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler(objectMapper))
                .build();
        if (authHeader != null && !authHeader.isEmpty()) {
            restTemplate.getInterceptors().add(
                    (outReq, bytes, clientHttpReqExec) -> {
                        outReq.getHeaders().set(
                                HttpHeaders.AUTHORIZATION, authHeader
                        );
                        return clientHttpReqExec.execute(outReq, bytes);
                    });
        }
        return restTemplate;
    }


    public static void main(String[] args) {
        SpringApplication.run(FrameworkManageApplication.class, args);
    }

}
