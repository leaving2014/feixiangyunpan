package com.fx.pan.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leaving
 * @date 2022/1/13 13:40
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class LoginUser implements UserDetails {


    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;


    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;


    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 权限列表
     */
    // @JSONField(serialize = false)
    private List<String> permissions;

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * 用户信息
     */
    private User user;


    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;

    public LoginUser(Long userId, User user) {
        this.userId = userId;
        this.user = user;
    }

    public LoginUser(User user, List<String> permissions) {
        this.user = user;
        this.permissions =  permissions;
    }

    public LoginUser(Long userId, User user, List<String> permissions) {
        this.userId = userId;
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 把permissions中 String类型的权限信息封装成SimpleGrantedAuthority对象
        // authorities = new ArrayList<>();
        // for (String permission : permissions) {
        //     SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission);
        //     authorityList.add(authority);
        // }
        if (authorities!=null){
            return authorities;
        }
        authorities =
                permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return authorities;

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
