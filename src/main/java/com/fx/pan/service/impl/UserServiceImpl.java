package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.common.Constants;
import com.fx.pan.common.Msg;
import com.fx.pan.component.UserDealComp;
import com.fx.pan.domain.LoginUser;
import com.fx.pan.domain.Storage;
import com.fx.pan.domain.User;
import com.fx.pan.mapper.StorageMapper;
import com.fx.pan.mapper.UserMapper;
import com.fx.pan.service.StorageService;
import com.fx.pan.service.UserService;
import com.fx.pan.utils.*;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author leaving
 * @Date 2022/1/14 10:48
 * @Version 1.0
 */

@Slf4j
@Service
@PropertySource(value = {"classpath:application.properties"})
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${fx.file.uploadFolder}")
    private String realBasePath;

    @Value("${fx.file.accessPath}")
    private String accessPath;
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisCache redisCache;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDealComp userDealComp;

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageMapper storageMapper;

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public Msg register(User user) {
        String msg = "";
        int code = 500;
        boolean userFlag = userDealComp.isUserNameExist(user);
        if (userFlag) {
            msg = "注册用户失败，用户名" + user.getUserName() + "已存在";
        } else {
            boolean flag = userMapper.insertUser(user) > 0;
            if (flag) {
                code = 0;
                msg = "注册成功";
                Storage storage = new Storage();
                storage.setUserId(user.getId());
                //添加用户存储空间信息
                // storageMapper.insert(storage);
                boolean b = storageService.insertUserStorage(storage);

            } else {
                msg = "注册失败";
            }
        }
        return Msg.msg(code, msg).put("ts", SysUtil.getTimeStamp());
    }


    /**
     * 用户登录
     * @param username,password
     * @return
     */
    @Override
    public Msg login(String username,String password) {
        // AuthenticationManager authenticate进行用户认证
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 如果认证没通过
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        //如果认证通过
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        User saveUserBean = findUserInfoByUserName(loginUser.getUsername());
        loginUser.setUserId(saveUserBean.getId());
        String jwt = JwtUtil.createJWT(JSONObject.toJSONString(loginUser));
        redisCache.setCacheObject(Constants.REDIS_LOGIN_USER_PREFIX + userId, loginUser);
        User user = loginUser.getUser();
        return  Msg.success("登录成功").put("token", jwt).put("userInfo",user).put("ts", SysUtil.getTimeStamp());
    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public Msg logout() {
        // 获取securityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        Long id = loginUser.getUser().getId();
        // 删除redis中的值
        redisCache.deleteObject(Constants.REDIS_LOGIN_USER_PREFIX+id);
        return  Msg.success("注销成功");
    }

    public User findUserInfoByUserName(String userName){
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserName, userName);
        return userMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public User seletUserWithUserName(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserName, username);
        return userMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @Override
    public Msg updateUser(User user) {

        int flag = userMapper.updateById(user);
        if (flag>0){
            return Msg.success("修改成功");
        }else{
            return Msg.error(500,"修改失败");
        }

    }

    @Override
    public User getUserBeanByToken(String token) {
        Claims c = null;
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        //        if (!token.startsWith("Bearer ")) {
//            throw new NotLoginException("token格式错误");
//        }
        token = token.replace("Bearer ", "");
        try {
            c = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            log.info("解码异常:" + e);
            return null;
        }
        if (c == null) {
            log.info("解码为空");
            return null;
        }
        String subject = c.getSubject();
        log.debug("解析结果：" + subject);
        User tokenUser = JSON.parseObject(subject, User.class);

        User saveUser = new User();
        String tokenPassword = "";
        String savePassword = "";
        if (StringUtils.isNotEmpty(tokenUser.getPassword())) {
            saveUser = seletUserWithUserName(tokenUser.getUserName());
            if (saveUser == null) {
                return null;
            }
            tokenPassword = tokenUser.getPassword();
            savePassword = saveUser.getPassword();
        }
        if (StringUtils.isEmpty(tokenPassword) || StringUtils.isEmpty(savePassword)) {
            return null;
        }
        if (tokenPassword.equals(savePassword)) {

            return saveUser;
        } else {
            return null;
        }
    }

    @Override
    public User selectUserById(Serializable id) {
        return userMapper.selectById(id);
    }

    @SneakyThrows
    @Override
    public String uploadAvatar(HttpServletRequest request, MultipartFile multipartFile) {
        // *图片扩展名*
        String imgSuffix = FileTypeUtils.getFileExtendName(multipartFile.getOriginalFilename());
        System.out.println("imgSuffix======" + imgSuffix);
        // *文件唯一的名字*
        String md5 = Md5Utils.getMd5(multipartFile);
        String fileName = md5 + "." + imgSuffix;
        Date todayDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = dateFormat.format(todayDate);
        // *域名访问的相对路径（通过浏览器访问的链接-虚拟路径）*
        String saveToPath = accessPath + today + "/";
        // *真实路径，实际储存的路径*
        String realPath = realBasePath + today + "/";
        // *储存文件的物理路径，使用本地路径储存*
        String filepath = realPath + fileName;
        logger.info("上传图片名为：" + fileName + "--虚拟文件路径为：" + saveToPath + "--物理文件路径为：" + realPath);
        // *判断有没有对应的文件夹*
        File destFile = new File(filepath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            multipartFile.transferTo(destFile);
        }
        return saveToPath + fileName;
    }

    @Override
    public List<User> getUserList(String query, int pageNum, int pageSize) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(query != null, User::getUserName, query);
        Page<User> page = new Page<>(pageNum, pageSize);
        List<User> records = page.getRecords();
        return records;
    }

    @Override
    public int adminRegister(User user) {
        boolean userFlag = userDealComp.isUserNameExist(user);
        if (userFlag) {
            return -1;
        } else {
            user.setRole(1);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            return userMapper.insert(user);
        }

    }

}
