package com.example.spring.cloud.demo.dto;

import org.springframework.lang.Nullable;

import java.util.Map;

public class UserDto {

    public UserDto(String name, String age){
        this.name = name;
        this.age = age;
    }
    private String name;
    private String age;

}
