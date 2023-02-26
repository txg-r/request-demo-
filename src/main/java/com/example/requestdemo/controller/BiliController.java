package com.example.requestdemo.controller;


import com.alibaba.excel.EasyExcel;
import com.example.requestdemo.domain.entity.Cdk;
import com.example.requestdemo.domain.entity.Group;
import com.example.requestdemo.domain.entity.ProjectProperties;
import com.example.requestdemo.domain.entity.Request;
import com.example.requestdemo.job.MainJob;
import com.example.requestdemo.domain.vo.ExcelCdk;
import com.example.requestdemo.pojo.AwardParamPojo;
import com.example.requestdemo.service.CdkService;
import com.example.requestdemo.util.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/Bzhan")
@Api(tags = "B站功能")
@Slf4j
public class BiliController {
    private MainJob job;
    private final List<String> msgList = new ArrayList<>();
    private final Random random = new Random();

    @Value("${project.awardOwner}")
    private String owner;
    @Autowired
    private ProjectProperties properties;

    @Autowired
    private CdkService cdkService;

    @PostConstruct
    private void init() {
        msgList.add("哈哈哈哈");
        msgList.add("主播牛的");
        msgList.add("捞");
        msgList.add("卧槽");
        msgList.add("!!!!!!");
        msgList.add("牛牛牛");
    }

    @Autowired
    public void setJob(MainJob job) {
        this.job = job;
        this.group = job.getGroup("B站");
    }

    private Group group;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/detail")
    @ApiOperation("获取兑换码数量")
    public Map<String,Integer> getAwardCount(){
        return cdkService.getAwardCount(owner);
    }

    @GetMapping("/yuanshi")
    @ApiOperation("抢原石")
    public void mainReward(String id) {
        System.out.println(1111);
        //设置group基本参数
        group.clear();
        group.setGlobalUrl("https://api.bilibili.com/x/activity/mission/task/reward/receive");
        group.setMethod("post");
        //设置结果处理中各种状况
        Map<Integer, String> states = new LinkedHashMap<>();
        states.put(0, "领取成功");
        states.put(75086, "已领取");
        states.put(75255, "领完了");
        states.put(75154, "领完了");


        //任务数量
        AtomicInteger taskNum = new AtomicInteger(group.getRequests().size());
        //开始时间
        long start = System.currentTimeMillis();
        //运行时间(5分钟)
        long duration = 1000 * 60 * 5;

        for (Request request : group.getRequests()) {
            job.getPool().submit(() -> {
                log.info("原石" + request.getRequestName() + "参数获取中...");
                //循环查询receive_id参数(是否能够领取)
                while (true) {
                    if (System.currentTimeMillis() > (start + duration)) {
                        break;
                    }
                    //发请求获取结果
                    AwardParamPojo awardParamPojo = null;
                    try {
                        awardParamPojo = getAwardParams(id, request);
                    } catch (Exception e) {
                        log.error("error", e);
                        try {
                            Thread.sleep((long) (Math.random() * 100) + 100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        continue;
                    }
                    Map<String, String> params = awardParamPojo.getParams();
                    //还不能领取,继续获取参数
                    if (params.get("receive_id").equals("0")) {
                        log.info("原石" + request.getRequestName() + "参数获取中...");
                        continue;
                    }
                    //已经领取过,直接结束
                    if (awardParamPojo.getData().get("receive_status").equals("3")) {
                        log.info("原石" + request.getRequestName() + "已领取");
                        return;
                    }
                    //设置参数
                    request.setParams(params);
                    request.setParamFromLib("csrf");
                    request.setHeaderFromLib("cookie");
                    log.info("原石" + request.getRequestName() + "获取参数成功");
                    break;
                }
                //开始发请求领取
                log.info("原石" + request.getRequestName() + "开始抢原石");
                HttpPost httpPost = HttpUtil.createPostByRequest(group, request);
                try (
                        CloseableHttpClient client = HttpClients.createDefault()
                ) {
                    while (true) {
                        CloseableHttpResponse response = response = client.execute(httpPost);
                        if (System.currentTimeMillis() > (start + duration)) {
                            taskNum.decrementAndGet();
                            break;
                        }
                        ObjectNode node = HttpUtil.handleResponse(response);
                        if (Objects.isNull(node)) {
                            continue;
                        }
                        int code = node.get("code").intValue();
                        String stateInfo = states.getOrDefault(code, null);
                        if (!Objects.isNull(stateInfo)) {
                            log.info(group.getGroupName() + request.getRequestName() + "原石" + "-----------" + stateInfo);
                            taskNum.decrementAndGet();
                            break;
                        }
                        log.info(group.getGroupName() + request.getRequestName() + "原石" + node.toString());
                    }
                } catch (IOException e) {
                    log.info(e.getClass().getName());
                }
                if (taskNum.get() <= 0) {
                    log.info(group.getGroupName() + "原石" + "任务完成");
                }
            });

        }
    }

    @GetMapping("/dayReward")
    @ApiOperation("每日奖励(1:开播60分钟,2:开播120分钟,3:10电池,4:弹幕六条,5:礼物两人,6:看十分钟")
    public void dayReward(String jobIndex) {
        group.clear();
        group.setGlobalUrl("https://api.bilibili.com/x/activity/mission/task/reward/receive");
        group.setMethod("post");
        group.setOnce(true);
        group.setInterval(1000);
        //初始化奖励map
        Map<String, String> rewards = properties.getRewardMap();
        Map<Integer, String> states = new LinkedHashMap<>();
        states.put(0, "领取成功");
        states.put(-400, "任务未完成");
        states.put(75086, "已领取");
        states.put(75255, "领完了");
        states.put(75154, "领完了");

        String job = rewards.getOrDefault(jobIndex, jobIndex);

        group.getRequests().forEach(r -> {
            r.setParams(getAwardParams(job, r).getParams());
            r.setHeaderFromLib("cookie");
            r.setParamFromLib("csrf");
        });

        this.job.init();
        //执行并定义处理函数
        this.job.handlerHttpGroups("B站", "日常奖励" + jobIndex, (response, name) -> {
            //结果处理
            ObjectNode node = HttpUtil.handleResponse(response);
            Assert.notNull(node, "response解析错误");
            int code = node.get("code").intValue();
            String stateInfo = states.getOrDefault(code, null);
            if (!Objects.isNull(stateInfo)) {
                log.info(name + "-----------" + stateInfo);
                return true;
            }
            log.info(name + "领取失败(再次尝试)" + node.toString());
            return false;
        });
    }

    @GetMapping("/start")
    @ApiOperation("开播(原神321,深空598,无期675,幻塔550,崩坏40,明日255)")
    public void startLive(String area_v2) {
        group.clear();
        group.setGlobalUrl("https://api.live.bilibili.com/room/v1/Room/startLive");
        group.setMethod("post");
        group.getGlobalParams().put("area_v2", area_v2);
        group.getGlobalParams().put("platform", "pc");
        group.getRequests().forEach(request -> {
            request.setHeaderFromLib("cookie");
            request.setParamFromLib("room_id");
            request.setParamFromLib("csrf_token");
            request.setParamFromLib("csrf");
        });
        job.init();
        job.handlerHttpGroups("B站", "开播", ((response, name) -> {
            //结果处理
            ObjectNode node = HttpUtil.handleResponse(response);
            Assert.notNull(node, "response解析错误");
            int code = node.get("code").intValue();
            if (code == 0) {
                JsonNode protocols = node.get("data").get("protocols");
                log.info(name + "开播成功");
                log.info("addr  :" + protocols.get(0).get("addr").asText());
                log.info("code  :" + protocols.get(0).get("code").asText());
                return true;
            }
            log.info(name + "开播失败" + node.toString());
            return true;
        }));
    }

    @GetMapping("/changeLive")
    @ApiOperation("切换直播")
    public void changeLive(String area_v2) {
        group.clear();
        group.setGlobalUrl("https://api.live.bilibili.com/room/v1/Room/update");
        group.setMethod("post");
        group.getGlobalParams().put("area_id", area_v2);
        group.getRequests().forEach(request -> {
            request.setHeaderFromLib("cookie");
            request.setParamFromLib("room_id");
            request.setParamFromLib("csrf_token");
            request.setParamFromLib("csrf");
        });
        job.init();
        job.handlerHttpGroups("B站", "切换直播", ((response, name) -> {
            //结果处理
            ObjectNode node = HttpUtil.handleResponse(response);
            Assert.notNull(node, "response解析错误");
            int code = node.get("code").intValue();
            if (code == 0) {
                log.info(name + "切换成功");
                return true;
            }
            log.info(name + "切换失败" + node.toString());
            return true;
        }));
    }

    @GetMapping("/sendMsg")
    @ApiOperation("发弹幕")
    public void sendMsg() {
        group.clear();
        group.setGlobalUrl("https://api.live.bilibili.com/msg/send");
        group.setMethod("post");
        group.setInterval(800);
        group.getGlobalParams().put("bubble", "0");
        group.getGlobalParams().put("color", "16777215");
        group.getGlobalParams().put("mode", "1");
        group.getGlobalParams().put("fontsize", "25");
        group.getGlobalParams().put("rnd", "1661353437");
        for (int i = 0; i < group.getRequests().size(); i++) {
            Request request = group.getRequests().get(i);
            request.setParamFromLib("csrf");
            request.setParamFromLib("csrf_token");
            request.setHeaderFromLib("cookie");
            String roomId;
            if (i == group.getRequests().size() - 1) {
                roomId = group.getRequests().get(0).getParamLib().get("room_id");
            } else {
                roomId = group.getRequests().get(i + 1).getParamLib().get("room_id");
            }

            request.getParams().put("roomid", roomId);
        }
        for (int i = 0; i < 6; i++) {
            group.getGlobalParams().put("msg", getRandMsg());
            job.init();
            job.handlerHttpGroups("B站", "第" + (i + 1) + "条弹幕", (response, name) -> {
                ObjectNode node = HttpUtil.handleResponse(response);
                Assert.notNull(node, "response解析错误");
                if (node.get("code").intValue() == 0) {
                    log.info(name + "发送成功");
                    return true;
                }
                log.info(name + "发送失败" + node.toString());
                return false;
            });
        }
    }

    @GetMapping("/sendGold")
    @ApiOperation("送礼物(数量填一半)")
    public void sendGold(int goldNum) {
        group.clear();
        //初始化全局参数
        group.setGlobalUrl("https://api.live.bilibili.com/xlive/revenue/v1/gift/sendGold");
        Map<String, String> globalParams = group.getGlobalParams();
        globalParams.put("gift_id", "31039");
        globalParams.put("send_ruid", "0");
        globalParams.put("gift_num", String.valueOf(goldNum));
        globalParams.put("coin_type", "gold");
        globalParams.put("bag_id", "0");
        globalParams.put("platform", "pc");
        globalParams.put("biz_code", "Live");
        globalParams.put("storm_beat_id", "0");
        globalParams.put("metadata", "");
        globalParams.put("price", "100");
        globalParams.put("visit_id", "bo0mf7yi7z41");
        //初始化参数,顺着送一轮
        for (int i = 0; i < group.getRequests().size(); i++) {
            Request request = group.getRequests().get(i);
            request.setParamFromLib("csrf");
            request.setParamFromLib("csrf_token");
            request.setParamFromLib("uid");
            request.setHeaderFromLib("cookie");
            String ruid;
            String biz_id;
            if (i == group.getRequests().size() - 1) {
                ruid = group.getRequests().get(0).getParamLib().get("uid");
                biz_id = group.getRequests().get(0).getParamLib().get("room_id");
            } else {
                ruid = group.getRequests().get(i + 1).getParamLib().get("uid");
                biz_id = group.getRequests().get(i + 1).getParamLib().get("room_id");
            }
            request.getParams().put("ruid", ruid);
            request.getParams().put("biz_id", biz_id);
        }
        job.init();
        job.handlerHttpGroups("B站", "顺着送一轮礼物", (response, name) -> {
            ObjectNode node = HttpUtil.handleResponse(response);
            Assert.notNull(node, "response解析错误");
            if (node.get("code").intValue() == 0) {
                log.info(name + "送礼物成功");
                return true;
            }
            log.info(name + "送礼物失败" + node.toString());
            return true;
        });
        //初始化参数,反着送一轮
        for (int i = 0; i < group.getRequests().size(); i++) {
            Request request = group.getRequests().get(i);
            request.setParamFromLib("csrf");
            request.setParamFromLib("csrf_token");
            request.setParamFromLib("uid");
            request.setHeaderFromLib("cookie");
            String ruid;
            String biz_id;
            if (i == 0) {
                ruid = group.getRequests().get(group.getRequests().size() - 1).getParamLib().get("uid");
                biz_id = group.getRequests().get(group.getRequests().size() - 1).getParamLib().get("room_id");
            } else {
                ruid = group.getRequests().get(i - 1).getParamLib().get("uid");
                biz_id = group.getRequests().get(i - 1).getParamLib().get("room_id");
            }
            request.getParams().put("ruid", ruid);
            request.getParams().put("biz_id", biz_id);
        }
        job.init();
        job.handlerHttpGroups("B站", "反着送一轮礼物", (response, name) -> {
            ObjectNode node = HttpUtil.handleResponse(response);
            Assert.notNull(node, "response解析错误");
            if (node.get("code").intValue() == 0) {
                log.info(name + "送礼物成功");
                return true;
            }
            log.info(name + "送礼物失败" + node.toString());
            return true;
        });

    }

    @GetMapping("/loadCdk")
    @ApiOperation("获取所有cdk,并载入数据库")
    public void loadCdk(String activity_id, HttpServletResponse response) throws IOException {
        group.clear();
        group.setGlobalUrl("https://api.bilibili.com/x/activity/rewards/awards/mylist");
        group.setMethod("get");
        group.setInterval(200);
        group.getGlobalParams().put("activity_id", activity_id);
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("text/html");
        response.setHeader("Content-Disposition", "attachment;fileName=B站cdk.txt");
        for (Request request : group.getRequests()) {
            request.setParamFromLib("csrf");
            request.setHeaderFromLib("cookie");
        }
        job.init();
        job.handlerHttpGroups("B站", "获取所有cdk", (biResponse, name) -> {
            ObjectNode node = HttpUtil.handleResponse(biResponse);
            Assert.notNull(node, "response解析错误");
            if (node.get("code").intValue() != 0) {
                log.info(name + "领取失败" + node.toString());
                return false;
            }
            try {
                outputStream.write(("\n" + name + "\n\n").getBytes(StandardCharsets.UTF_8));
                JsonNode rewardList = node.get("data").get("list");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
                ArrayList<Cdk> saveCdkList = new ArrayList<>();
                for (JsonNode rewardNode : rewardList) {
                    String award_name = rewardNode.get("award_name").asText();
                    String cdk = rewardNode.get("extra_info").get("cdkey_content").asText();
                    Date receive_time = new Date(rewardNode.get("receive_time").asLong() * 1000);
                    String description = rewardNode.get("description").asText();
                    outputStream.write((award_name + ":\t\t" + cdk + ":\t\t" + dateFormat.format(receive_time) + "\n")
                            .getBytes(StandardCharsets.UTF_8));
                    saveCdkList.add(new Cdk(
                            cdk,
                            award_name,
                            receive_time,
                            description,
                            "tyf",
                            false
                    ));
                }
                cdkService.saveBatchDistinct(saveCdkList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;

        });
        outputStream.close();

    }

    @GetMapping("/getCdk")
    @ApiOperation("取出指定的cdk")
    @Transactional
    public void download(HttpServletResponse response, @RequestParam String awardName, @RequestParam int awardNum) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(awardName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelCdk.class).sheet("cdk").doWrite(cdkService.getCdk(awardName,awardNum));
        log.info("取出"+awardName+awardNum);
    }

    private AwardParamPojo getAwardParams(String awardId, Request r) {
        //封装请求参数
        Map<String, String> params = new HashMap<>();
        params.put("id", awardId);
        params.put("csrf", r.getParamLib().get("csrf"));
        Map<String, String> headers = new HashMap<>();
        headers.put("cookie", r.getHeaderLib().get("cookie"));

        //发请求获取结果
        ObjectNode node = HttpUtil.handleGet("https://api.bilibili.com/x/activity/mission/single_task", params, headers);

        //解析结果,找到需要的参数
        assert node != null;
        JsonNode taskInfo = node.get("data").get("task_info");
        JsonNode groupNode = taskInfo.get("group_list").get(0);

        //封装结果
        HashMap<String, String> paramsResult = new HashMap<>();
        paramsResult.put("act_id", String.valueOf(groupNode.get("act_id").intValue()));
        paramsResult.put("task_id", String.valueOf(groupNode.get("task_id").intValue()));
        paramsResult.put("group_id", String.valueOf(groupNode.get("group_id").intValue()));
        paramsResult.put("receive_id", String.valueOf(taskInfo.get("receive_id").intValue()));
        paramsResult.put("receive_from", "missionPage");
        HashMap<String, String> dataResult = new HashMap<>();
        dataResult.put("receive_status", String.valueOf(taskInfo.get("receive_status").intValue()));


        return new AwardParamPojo(paramsResult, dataResult);
    }

    private String getRandMsg() {
        return msgList.get(random.nextInt(6));
    }


}
