package com.example.spring.cloud.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.spring.cloud.demo.command.UserCommad;
import com.example.spring.cloud.demo.dto.UserDto;
import com.example.spring.cloud.demo.exception.LclException;
import com.example.spring.cloud.demo.exception.QmmException;
import com.netflix.hystrix.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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


    /**
     * 获取用户统一入口
     * @param id
     * @param type
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @HystrixCommand(fallbackMethod = "getUserBack",ignoreExceptions = QmmException.class)
    public String getUser(String id,String type) throws ExecutionException, InterruptedException {
        switch (type){
            //通过继承实现同步调用
            case "1":
                return getImplSync(id);
            //通过继承实现异步调用
            case "2":
                return getImplAsync(id);
            //通过注解实现同步调用
            case "3":
                return getAnnotationSync(id);
            //通过注解实现异步调用
            case "4":
                return getAnnotationAsync(id);
            case "101":
                throw new LclException();
            case "102":
                throw new QmmException();
            case "103":
                throw new HystrixBadRequestException("");
            case "104":
                throw new RuntimeException("Exception 104 code!!!");
                default:
                   throw new InterruptedException("");
        }
    }

    /**
     * 自定义Setter
     * @return
     */
    public com.netflix.hystrix.HystrixCommand.Setter getSetter(){
        return com.netflix.hystrix.HystrixCommand.Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("demoGroup"));
    }

    /**
     * 自定义Setter
     * @return
     */
    public com.netflix.hystrix.HystrixCommand.Setter getSetterAsCommandKey(){
        return com.netflix.hystrix.HystrixCommand.Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("demoGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("CommandKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("demoThreadPoolKey"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000));
    }

    /**
     * 通过继承实现同步调用
     * @param id
     * @return
     */
    public String getImplSync(String id) {
        log.info("通过继承实现同步调用;id={}",id);
        return (String) new UserCommad(getSetter(),restTemplate,1).execute();
    }

    /**
     * 通过继承实现异步调用
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public String getImplAsync(String id) throws ExecutionException, InterruptedException {
        log.info("通过继承实现异步调用;id={}",id);
        Future<String> userDtoFuture = new UserCommad(getSetter(),restTemplate,1).queue();
        return userDtoFuture.get();
    }

    /**
     * 通过注解实现同步调用
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @HystrixCommand(fallbackMethod = "getUserBack",groupKey = "userGroup",commandKey = "getAnnotationSync",threadPoolKey = "getUserByIdThread")
    public String getAnnotationSync(String id) {
        log.info("通过注解实现同步调用;id={}",id);
        return restTemplate.getForObject("http://EUREKA-CLIENT/v1/getUser/{1}",String.class,id);
    }

    /**
     * 通过注解实现异步调用
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @HystrixCommand(fallbackMethod = "getUserBack")
    public String getAnnotationAsync(String id){
        log.info("通过注解实现异步调用;id={}",id);
        AsyncResult<String> asyncResult = new AsyncResult<String>(){
            @Override
            public String invoke() {
                return restTemplate.getForObject("http://EUREKA-CLIENT/v1/getUser/{1}",String.class, (Object) id);
            }

            @Override
            public String get() {
                return invoke();
            }
        };
        return asyncResult.get();
    }

    /**
     * 外层类型异常回调方法
     * @return
     */
    public String getUserBack(String id,String type,Throwable throwable){
        assert "Exception 104 code!!!".equals(throwable.getMessage());
        return JSON.toJSONString(new UserDto(1L,"调用错误","类型错误"));
    }

    /**
     * 外层类型异常回调方法
     * @return
     */
    public String getUserBack(String id,String type){
        return JSON.toJSONString(new UserDto(1L,"调用错误","类型错误"));
    }

    /**
     * 调用异常回调方法
     * @param id
     * @return
     */
    public String getUserBack(String id){
        return JSON.toJSONString(new UserDto(1L,"调用错误","年龄未知"));
    }


    /**
     * 获取Observable统一入口
     * @param id
     * @param type
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Observable getObservable(String id,String type) throws ExecutionException, InterruptedException {
        switch (type){
            //通过继承实现Hot Observable调用
            case "1":
                return getImplHotObservable(id);
            //通过继承实现Cold Observable调用
            case "2":
                return getImplColdObservable(id);
            //通过注解实现Hot Observable调用
            case "3":
                return getAnnotationHotObservable(id);
            //通过注解实现Cold Observable调用
            case "4":
                return getAnnotationColdObservable(id);
            default:
                return null;
        }

    }


    /**
     *
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Observable getImplHotObservable(String id) throws ExecutionException, InterruptedException {
        Observable observable = new UserCommad(getSetter(), restTemplate,1L).observe();
        return observable;
    }

    /**
     *
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Observable getImplColdObservable(String id) throws ExecutionException, InterruptedException {
        Observable observable = new UserCommad(getSetter(), restTemplate,1L).toObservable();
        return observable;
    }

    /**
     * 使用
     * @param id
     * @return
     */
    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER)
    public Observable getAnnotationHotObservable(String id) {
        return this.getObservable(id);
    }

    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.LAZY)
    public Observable getAnnotationColdObservable(String id) {
        return this.getObservable(id);
    }

    public Observable getObservable(String id){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try{
                    if(!subscriber.isUnsubscribed()){
                        String userDto = restTemplate.getForObject("http://EUREKA-CLIENT/v1/getUser/{1}",String.class,id);
                        subscriber.onNext(userDto);
                        subscriber.onCompleted();
                    }
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

}
