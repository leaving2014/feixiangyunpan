package com.fx.pan.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (Excel)表实体类
 *
 * @author leaving
 * @since 2022-04-05 13:20:16
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("excel")
public class ExcelBean implements Serializable, Cloneable {

    @TableId(type = IdType.AUTO)
    private Long id;
    // 表格文件名
    private String title;
    //表格json数据
    private String data;
    //唯一识别码
    private String identifier;
    //协同编辑(默认0,不开启, 1:开启)
    private Integer collaborate;
    //协同编辑用户权限(0: 所有权限,包含编辑和下载, 1: 编辑,2:只读)
    private Integer permissions;
    //最大协同编辑人数(默认为100)
    private Integer maxNumEditors;
    //类型(1:网盘文件数据,2: 在线编辑数据)
    private Integer type;
    //文件id(网盘文件id)
    private Long fileId;

    private Date createTime;

    private Date updateTime;

    private Long userId;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

