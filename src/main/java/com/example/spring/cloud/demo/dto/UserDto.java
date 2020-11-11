package com.example.spring.cloud.demo.dto;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Map;

@Data
@AllArgsConstructor
public class UserDto {

    private long id;
    private String name;
    private String age;

}
