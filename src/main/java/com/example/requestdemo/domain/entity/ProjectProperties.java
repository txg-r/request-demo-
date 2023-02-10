package com.example.requestdemo.domain.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {
    private String jsonPath;

    private Map<String, String> rewardMap;
}
