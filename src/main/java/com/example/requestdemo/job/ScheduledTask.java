package com.example.requestdemo.job;

import com.example.requestdemo.controller.BiliController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    @Autowired
    private BiliController biliController;

    @Scheduled(cron = "00 00 2 9 12 ?")
    public void doBiliDay3() {
        biliController.mainReward("0f4416e8");
    }

    @Scheduled(cron = "59 59 23 9 12 ?")
    public void doBiliDay4() {
        biliController.mainReward("0f4416e8");
    }

    @Scheduled(cron = "00 00 2 11 12 ?")
    public void doBiliDay5() {
        biliController.mainReward("93617a9d");
    }

    @Scheduled(cron = "59 59 23 11 12 ?")
    public void doBiliDay6() {
        biliController.mainReward("93617a9d");
    }

    @Scheduled(cron = "00 00 2 16 12 ?")
    public void doBiliDay10() {
        biliController.mainReward("c84a4da4");
    }

    @Scheduled(cron = "59 59 23 16 12 ?")
    public void doBiliDay11() {
        biliController.mainReward("c84a4da4");
    }

    @Scheduled(cron = "00 00 2 26 12 ?")
    public void doBiliDay20() {
        biliController.mainReward("3cba765a");
    }

    @Scheduled(cron = "59 59 23 26 12 ?")
    public void doBiliDay21() {
        biliController.mainReward("3cba765a");
    }

    @Scheduled(cron = "00 00 2 5 1 ?")
    public void doBiliDay30() {
        biliController.mainReward("f800f808");
    }

    @Scheduled(cron = "59 59 23 5 1 ?")
    public void doBiliDay31() {
        biliController.mainReward("f800f808");
    }

    @Scheduled(cron = "00 00 2 15 1 ?")
    public void doBiliDay40() {
        biliController.mainReward("9976d3b4");
    }

    @Scheduled(cron = "59 59 23 15 1 ?")
    public void doBiliDay41() {
        biliController.mainReward("9976d3b4");
    }

    //发弹幕并领奖励
    @Scheduled(cron = "20 1 0 * * *")
    public void danmu() throws InterruptedException {
        biliController.sendMsg();
    }

    @Scheduled(cron = "20 2 0 * * *")
    public void danmuReward() throws InterruptedException {
        biliController.dayReward("4");
    }



    //送礼物并领奖励
    @Scheduled(cron = "20 3 0 * * *")
    public void gold() throws InterruptedException {
        biliController.sendGold(5);
    }

    @Scheduled(cron = "50 3 0 * * *")
    public void goldReward1() throws InterruptedException {
        biliController.dayReward("3");
    }

    @Scheduled(cron = "30 4 0 * * *")
    public void goldReward2() throws InterruptedException {
        biliController.dayReward("5");
    }

    //领取10分钟任务
    @Scheduled(cron = "0 20 0 * * *")
    public void get10() {
        biliController.dayReward("6");
    }

    //领取60分钟任务
    @Scheduled(cron = "0 10 1 * * *")
    public void get60() {
        biliController.dayReward("1");
    }

    //领取120分钟任务
    @Scheduled(cron = "0 10 2 * * *")
    public void get120() {
        biliController.dayReward("2");
    }

}