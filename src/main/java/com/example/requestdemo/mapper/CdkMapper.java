package com.example.requestdemo.mapper;

import com.example.requestdemo.domain.entity.AwardCount;
import com.example.requestdemo.domain.entity.Cdk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;

import java.util.List;
import java.util.Map;

/**
* @author huoer
* @description 针对表【cdk】的数据库操作Mapper
* @createDate 2023-02-10 18:08:30
* @Entity com.example.requestdemo.domain.entity.Cdk
*/
public interface CdkMapper extends BaseMapper<Cdk> {

    List<AwardCount> getAWardCount(@Param("owner") String owner);
}




