package com.example.requestdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class AwardParamPojo {
    private Map<String, String> params;
    private Map<String, String> data;
}
