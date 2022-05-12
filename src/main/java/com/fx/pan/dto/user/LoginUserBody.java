package com.fx.pan.dto.user;

import lombok.Data;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/5/10 23:28
 */

@Data
public class LoginUserBody {
    private String userName;
    private String password;
    private String captcha;
    private String ts;
    private String sign;

}
