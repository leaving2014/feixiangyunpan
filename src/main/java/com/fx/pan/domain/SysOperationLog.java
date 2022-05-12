package com.fx.pan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/5/12 8:46
 */
@TableName("sys_log")
// @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SysOperationLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    //操作方法
    private String operationMethod;

    //操作简称
    private String operationDesc;

    //请求url
    private String url;

    //参数
    private String parameter;

    //ip
    private String ip;

    //耗时
    private Integer timeConsuming;

    //操作时间
    private Date createTime;

    //日志类型 1:正常操作日志 2:错误日志
    private byte logType;

    //错误日志msg
    private String errorLogMsg;

    //操作类型
    private String operationType;

    //用户id
    private Long userId;

    //用户名
    private String userName;
}
