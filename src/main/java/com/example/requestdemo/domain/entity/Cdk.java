package com.example.requestdemo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName cdk
 */
@TableName(value ="cdk")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cdk implements Serializable {
    /**
     * cdk
     */
    @TableId
    private String cdkId;

    /**
     * 奖励名
     */
    private String awardName;

    /**
     * 领取时间
     */
    private Date receiveTime;

    /**
     * 奖励描述
     */
    private String description;

    /**
     * 所属人
     */
    private String owner;

    private Boolean isSell;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}