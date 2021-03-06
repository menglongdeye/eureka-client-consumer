package com.example.spring.cloud.demo.api;

import com.example.spring.cloud.demo.command.UserCommad;
import com.example.spring.cloud.demo.dto.UserDto;
import com.example.spring.cloud.demo.service.HelloService;
import com.netflix.discovery.converters.Auto;
import com.netflix.hystrix.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/v1")
public class HelloApi {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/helloConsumer")
    public String getStr() {

        return restTemplate.getForEntity("http://EUREKA-CLIENT/v1/hello", String.class).getBody();
    }


    /**
     *
     * @param id
     * @param type
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/getUserById")
    public String getUserById(String id,String type) throws ExecutionException, InterruptedException,Exception {
        return helloService.getUser(id, type);
    }

    @GetMapping("/getObservable")
    public Observable getObservable(String id, String type) throws ExecutionException, InterruptedException {
        return helloService.getObservable(id, type);
    }



    @Autowired
    private HelloService helloService;

    @GetMapping("/helloConsumer1")
    public String getStr1(String d) {
        return helloService.getStr();
    }


    /**
     * restTemplate请求类型
     */
    public void demo() {

        //getForEntity使用demo
        ResponseEntity responseEntity = restTemplate.getForEntity("http://EUREKA-CLIENT/v1/hello?name={1}&age={2}", String.class, "demoName", 18);
        ResponseEntity<UserDto> responseEntity1 = restTemplate.getForEntity("http://EUREKA-CLIENT/v1/hello?name={1}", UserDto.class, "demoName");
        Map map = new HashMap<String, String>();
        map.put("name", "demoname");
        map.put("age", "15");
        ResponseEntity<UserDto> responseEntity2 = restTemplate.getForEntity("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", UserDto.class, map);
        ResponseEntity<String> responseEntity3 = restTemplate.getForEntity("http://EUREKA-CLIENT/v1/hello}", String.class);
        //getForObject使用demo
        UserDto userDto = restTemplate.getForObject("http://EUREKA-CLIENT/v1/hello", UserDto.class);
        UserDto userDto1 = restTemplate.getForObject("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", UserDto.class, "demoName", 18);
        UserDto userDto2 = restTemplate.getForObject("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", UserDto.class, map);

        //postForEntity使用demo
        UserDto userDto3 = new UserDto("demoname", "15");
        ResponseEntity<String> userDtoResponseEntity = restTemplate.postForEntity("http://EUREKA-CLIENT/v1/hello", userDto3, String.class);
        String body = userDtoResponseEntity.getBody();
        ResponseEntity<UserDto> userDtoResponseEntity2 = restTemplate.postForEntity("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, UserDto.class, "demoname", "15");
        ResponseEntity<UserDto> userDtoResponseEntity3 = restTemplate.postForEntity("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, UserDto.class, map);
        //postForObject使用demo
        String body1 = restTemplate.postForObject("http://EUREKA-CLIENT/v1/hello", userDto3, String.class);
        String body2 = restTemplate.postForObject("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, String.class, "demoname", "15");
        String body3 = restTemplate.postForObject("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, String.class, map);

        //postForLocation使用样例
        URI uri1 = restTemplate.postForLocation("http://EUREKA-CLIENT/v1/hello", userDto3);
        URI uri2 = restTemplate.postForLocation("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, "demoname", "15");
        URI uri3 = restTemplate.postForLocation("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, map);

        //put使用样例
        restTemplate.put("http://EUREKA-CLIENT/v1/hello", userDto3);
        restTemplate.put("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, UserDto.class, "demoname", "15");
        restTemplate.put("http://EUREKA-CLIENT/v1/hello?name={name}&age={age}", userDto3, UserDto.class, map);

        //delete使用样例
        restTemplate.delete("http://EUREKA-CLIENT/v1/hello");
        restTemplate.delete("http://EUREKA-CLIENT/v1/hello", "demoname", "15");
        restTemplate.delete("http://EUREKA-CLIENT/v1/hello", map);
    }
}

