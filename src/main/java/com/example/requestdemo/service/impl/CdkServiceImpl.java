package com.example.requestdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.requestdemo.domain.entity.Cdk;
import com.example.requestdemo.domain.vo.ExcelCdk;
import com.example.requestdemo.service.CdkService;
import com.example.requestdemo.mapper.CdkMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        if (baseMapper.selectById(cdk.getCdkId()) != null) {
            return;
        }
        baseMapper.insert(cdk);
    }

    @Override
    public List<ExcelCdk> getCdk(String awardName, int awardNum) {
        LambdaQueryWrapper<Cdk> wrapper = new LambdaQueryWrapper<Cdk>().like(Cdk::getAwardName, awardName).last("limit " + awardNum);
        return baseMapper.selectList(wrapper).stream().map(cdk -> {
            cdk.setIsSell(true);
            baseMapper.updateById(cdk);
            return new ExcelCdk(cdk.getAwardName(), cdk.getCdkId());
        }).collect(Collectors.toList());
    }
}




