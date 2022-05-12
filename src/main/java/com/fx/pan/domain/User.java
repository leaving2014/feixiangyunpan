package com.fx.pan.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author leaving
 * @date 2021/11/24 21:21
 * @version 1.0
 */

@EqualsAndHashCode(callSuper = false)
@TableName("user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //用户名
    private String userName;
    //密码
    @JSONField(serialize = false)
    private String password;
    //昵称
    private String nickName;
    //头像地址
    private String avatar;
    //手机号
    private String phoneNumber;
    //邮箱
    private String email;
    //性别(0男，1女，2未知）
    private Integer sex;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //角色 (1管理员2普通用户)
    private Integer role;
    //账号状态（ 0正常1停用）
    private Integer status;

    private Integer deleted;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", sex=" + sex +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", role=" + role +
                ", status=" + status +
                ", deleted=" + deleted +
                '}';
    }
}
