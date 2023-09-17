package com.example.feigndemo.controller;

import com.example.feigndemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("sayHello")
    public String sayHello() {
        return userService.sayHello();
    }

    @GetMapping("hello")
    public String hello() {
        try {
            // 测试feign使用hystrix的熔断机制
            Thread.sleep(5000);
        } catch (Exception ignore) {

        }
        return "hello world";
    }
}
