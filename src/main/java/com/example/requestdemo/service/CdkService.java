package com.example.requestdemo.service;

import com.example.requestdemo.domain.entity.Cdk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.requestdemo.domain.vo.ExcelCdk;

import java.util.List;

/**
* @author huoer
* @description 针对表【cdk】的数据库操作Service
* @createDate 2023-02-10 18:08:30
*/
public interface CdkService extends IService<Cdk> {

    void saveDistinct(Cdk cdk);

    List<ExcelCdk> getCdk(String awardName, int awardNum);
}
