package com.example.spring.cloud.demo.command;

import com.example.spring.cloud.demo.dto.UserDto;
import com.netflix.hystrix.HystrixObservableCommand;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

public class UserObservableCommad extends HystrixObservableCommand<UserDto> {

    private RestTemplate restTemplate;
    private long id;

    protected UserObservableCommad(Setter setter, RestTemplate restTemplate, Long id) {
        super(setter);
        this.restTemplate = restTemplate;
        this.id = id;
    }

    @Override
    protected Observable<UserDto> construct() {
        return Observable.create(new Observable.OnSubscribe<UserDto>() {
            @Override
            public void call(Subscriber<? super UserDto> subscriber) {
                try{
                    if(!subscriber.isUnsubscribed()){
                        UserDto userDto = restTemplate.getForObject("http://EUREKA-CLIENT/v1/getUser/{1}",UserDto.class,id);
                        subscriber.onNext(userDto);
                        subscriber.onCompleted();
                    }
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }


    @Override
    protected Observable<UserDto> resumeWithFallback() {
        return Observable.error(new UnsupportedOperationException("No fallback available."));
    }
}
