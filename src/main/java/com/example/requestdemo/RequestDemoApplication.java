package com.example.requestdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.example.requestdemo.mapper")
public class RequestDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestDemoApplication.class, args);
    }

}
