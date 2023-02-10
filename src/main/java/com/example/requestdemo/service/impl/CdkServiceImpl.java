package com.example.requestdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.requestdemo.domain.entity.Cdk;
import com.example.requestdemo.service.CdkService;
import com.example.requestdemo.mapper.CdkMapper;
import org.springframework.stereotype.Service;

/**
* @author huoer
* @description 针对表【cdk】的数据库操作Service实现
* @createDate 2023-02-10 18:08:30
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




