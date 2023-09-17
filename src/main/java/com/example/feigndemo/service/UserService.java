package com.example.feigndemo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "userService", fallback = UserFallBack.class)
public interface UserService {

    @GetMapping("hello")
    String sayHello();
}
