package com.example.feigndemo.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HystrixController {

    // 信号量隔离略，控制并发请求或者并发降级，使用semaphore
    // 线程池隔离

    /**
     * 优点：提高并发能力，接口调用隔离保证系统的稳定性。
     * 缺点：开启多个线程池增加cpu的开销
     * 使用场景：服务多且负载高，可以使用。如果服务负载不高没上么访问量，不必用线程池隔离技术
     */
    @RequestMapping("test3")
    @HystrixCommand(
            fallbackMethod = "hystrixDemo2Downgrade", groupKey = "HystrixController", commandKey = "hystrixDemo3",
            threadPoolKey = "thread-pool-hystrix-", threadPoolProperties = {
            // 线程池核心数
            @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "5"),
            // 队列 -1表示内存队列、正数表示LinkedQueue大小
            @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "10"),
            // 控制排队最大多少，多了降级
            @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "8"),
            // 核心线程存活时间
            @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "2"),
            // 打开超时中断
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_INTERRUPT_ON_TIMEOUT, value = "true"),
            // 单个线程执行任务超时时间
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "1000")}
    )
    public String hystrixDemo3() {
        System.out.println("当前线程：" + Thread.currentThread().getName());
        try {
            // 检查线程池超时中断
            Thread.sleep(2000);
        } catch (Exception ignored) {
        }
        return "demo3";
    }


    //  服务熔断DEMO

    /**
     * HystrixPropertiesManager 相关的属性
     * 时间窗
     */
    @RequestMapping("/test2")
    @HystrixCommand(fallbackMethod = "hystrixDemo2Downgrade", commandProperties = {
            // 开启熔断
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
            // 一个时间窗，允许发生远程调用错误的次数阈值，达到开启熔断。窗口默认10s，错误数默认20
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "2"),
            // 一个时间窗，允许发生远程调用错误的百分比，达到开启熔断。错误百分比默认50%
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50"),
            // 开启熔断后，多少毫秒内不发起请求，直接熔断。恢复时间默认5s
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "5000"),
            // 时间窗大小，默认10s，这个动态调配可以不用配置。
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "10000")
    })
    public String hystrixDemo2() {
        System.out.println("test2 接口被调用");
        try {
            // 错误达到阈值，就不再调用接口，等恢复后再次调用
            Thread.sleep(5000);
        } catch (Exception ignored) {
        }
        return "demo2";
    }

    // 熔断方法
    public String hystrixDemo2Downgrade() {
        return "demo2 downGrade";
    }

    //  服务降级DEMO

    /**
     * HystrixCommand 超时默认 1s
     * 属性
     * fallbackMethod：远程服务不存在或者超时、调用该降级方法并返回
     */
    @RequestMapping("/test1")
    @HystrixCommand(fallbackMethod = "hystrixDemo1Downgrade")
    public String hystrixDemo1() {
        try {
            System.out.println("test1 接口被调用");
            Thread.sleep(5000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("demo1");
        return "demo1";
    }

    // 降级方法，和原方法一样的返回结果类型
    // 参数和对应的方法一致
    public String hystrixDemo1Downgrade() {
        return "demo1 downGrade";
    }
}
