package com.example.requestdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.requestdemo.domain.Cdk;
import com.example.requestdemo.service.CdkService;
import com.example.requestdemo.mapper.CdkMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
* @author tyfff
* @description 针对表【cdk】的数据库操作Service实现
* @createDate 2023-02-09 23:48:40
*/
@Service
public class CdkServiceImpl extends ServiceImpl<CdkMapper, Cdk>
    implements CdkService {

    @Override
    public void saveDistinct(Cdk cdk) {
        if (baseMapper.selectById(cdk.getCdkId())!=null) {
            return;
        }

        baseMapper.insert(cdk);
    }
}




