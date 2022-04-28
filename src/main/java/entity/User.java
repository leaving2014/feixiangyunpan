package entity;

import java.util.Date;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 用户表(User)表实体类
 *
 * @author leaving
 * @since 2022-04-25 19:02:40
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //主键
    private Long id;
    //用户名
    private String userName;
    //密码
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



    
    }

