package com.example.requestdemo.domain.job;

import com.example.requestdemo.controller.BiliController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    @Autowired
    private BiliController biliController;

}