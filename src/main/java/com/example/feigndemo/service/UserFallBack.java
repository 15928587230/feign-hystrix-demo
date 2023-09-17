package com.example.feigndemo.service;

import org.springframework.stereotype.Component;

// 服务降级处理
@Component
public class UserFallBack implements UserService {
    @Override
    public String sayHello() {
        return "sayHello fallback";
    }
}
