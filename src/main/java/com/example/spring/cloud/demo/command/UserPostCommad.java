package com.example.spring.cloud.demo.command;

import com.alibaba.fastjson.JSON;
import com.example.spring.cloud.demo.dto.UserDto;
import com.example.spring.cloud.demo.exception.LclException;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import org.springframework.web.client.RestTemplate;

public class UserPostCommad extends HystrixCommand<String> {

    private RestTemplate restTemplate;
    private UserDto userDto;

    public UserPostCommad(RestTemplate restTemplate, UserDto userDto) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Set")));
        this.restTemplate = restTemplate;
        this.userDto = userDto;
    }

    @Override
    protected String run() throws Exception {
        restTemplate.postForObject("http://EUREKA-CLIENT/v1/setUser",userDto,UserDto.class);
        UserCommad.flushCache(String.valueOf(userDto.getId()));
        return null;
    }

}
