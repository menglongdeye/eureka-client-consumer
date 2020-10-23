package com.example.spring.cloud.demo.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HelloService {
    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallBack")
    public String getStr() {
        long start = System.currentTimeMillis();
        String str = restTemplate.getForEntity("http://EUREKA-CLIENT/v1/hello", String.class).getBody();
        log.info("time:【{}】", System.currentTimeMillis() - start);
        return str;
    }

    public String fallBack() {
        return "error";
    }
}
