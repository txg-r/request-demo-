package com.example.requestdemo.service;

import com.example.requestdemo.domain.entity.Cdk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.requestdemo.domain.vo.ExcelCdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author huoer
* @description 针对表【cdk】的数据库操作Service
* @createDate 2023-02-10 18:08:30
*/
public interface CdkService extends IService<Cdk> {
    List<ExcelCdk> getCdk(String awardName, int awardNum);

    Map<String, Integer> getAwardCount(String owner);

    void saveBatchDistinct(ArrayList<Cdk> saveCdkList);
}
