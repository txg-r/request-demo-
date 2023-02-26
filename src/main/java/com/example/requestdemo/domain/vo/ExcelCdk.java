package com.example.requestdemo.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelCdk {

    @ExcelProperty(value = "奖励名称")
    private String awardName;

    @ExcelProperty(value = "兑换码")
    private String cdk;
}
