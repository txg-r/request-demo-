package com.example.requestdemo.service;

import com.example.requestdemo.domain.Cdk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author tyfff
* @description 针对表【cdk】的数据库操作Service
* @createDate 2023-02-09 23:48:40
*/
public interface CdkService extends IService<Cdk> {

    void saveDistinct(Cdk cdk);
}
