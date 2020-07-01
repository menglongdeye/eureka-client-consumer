package com.example.spring.cloud.demo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/v1")
public class HelloApi {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/helloConsumer")
    public String getStr(){
        return restTemplate.getForEntity("http://EUREKA-CLIENT/v1/hello",String.class).getBody();
    }
}

