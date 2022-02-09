package com.fx.pan.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fx.pan.domain.User;
import com.fx.pan.mapper.UserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author leaving
 */

@Component
public class UserDealComp {
    @Resource
    UserMapper userMapper;


    /**
     * 检测用户名是否存在
     *
     * @param user
     */
    public Boolean isUserNameExist(User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserName, user.getUserName());
        List<User> list = userMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测手机号是否存在
     *
     * @param user
     * @return
     */
    public Boolean isPhoneExist(User user) {

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPhoneNumber, user.getPhoneNumber());
        List<User> list = userMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

}
