package com.fx.pan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fx.pan.domain.User;
import org.springframework.stereotype.Repository;

/**
 * @Author leaving
 * @Date 2022/1/14 10:40
 * @Version 1.0
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(User user);


    public int checkUserNameUnique(String userName);

    int updateUser(User user);
}
