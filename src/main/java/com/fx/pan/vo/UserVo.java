package com.fx.pan.vo;

import lombok.Data;

/**
 * @author leaving
 * @date 2022/4/8 15:57
 * @version 1.0
 */

@Data
public class UserVo {
    private Long id;
    private String userName;
    private String nickName;
    private String email;
    private String avatar;

}
