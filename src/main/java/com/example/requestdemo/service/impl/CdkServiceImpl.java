package com.example.requestdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.requestdemo.domain.entity.AwardCount;
import com.example.requestdemo.domain.entity.Cdk;
import com.example.requestdemo.domain.vo.ExcelCdk;
import com.example.requestdemo.service.CdkService;
import com.example.requestdemo.mapper.CdkMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<ExcelCdk> getCdk(String awardName, int awardNum) {
        LambdaQueryWrapper<Cdk> wrapper = new LambdaQueryWrapper<Cdk>()
                .like(Cdk::getAwardName, awardName)
                .eq(Cdk::getIsSell,false)
                .last("limit " + awardNum);
        List<Cdk> cdks = baseMapper.selectList(wrapper);
        return cdks.stream().map(cdk -> {
            cdk.setIsSell(true);
            baseMapper.updateById(cdk);
            return new ExcelCdk(cdk.getAwardName(), cdk.getCdkId());
        }).collect(Collectors.toList());

    }

    @Override
    public Map<String, Integer> getAwardCount(String owner) {
        HashMap<String, Integer> result = new HashMap<>();
        return baseMapper.getAWardCount(owner).stream().collect(Collectors.toMap(AwardCount::getAwardName, AwardCount::getCount));
    }

    @Override
    public void saveBatchDistinct(ArrayList<Cdk> saveCdkList) {
        if (!saveCdkList.isEmpty()){
            baseMapper.insertBatchDistinct(saveCdkList);
        }
    }
}




