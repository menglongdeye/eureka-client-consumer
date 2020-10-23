package com.example.spring.cloud.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//开启服务发现
@EnableDiscoveryClient
@SpringBootApplication
//开启断路器
@EnableCircuitBreaker
//以上所有配置可以统一使用@SpringCloudApplication替换
public class SpringCloudDemoApplication {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudDemoApplication.class, args);
    }

}
