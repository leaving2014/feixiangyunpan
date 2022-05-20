package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fx.pan.domain.*;
import com.fx.pan.common.Constants;
import com.fx.pan.component.UserDealComp;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leaving
 * @date 2022/1/14 10:48
 * @version 1.0
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
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDealComp userDealComp;

    @Resource
    private StorageService storageService;

    @Resource
    private StorageMapper storageMapper;

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public ResponseResult register(User user) {
        String msg = "";
        int code = 500;
        boolean userFlag = userDealComp.isUserNameExist(user);
        if (userFlag) {
            msg = "注册用户失败，用户名" + user.getUserName() + "已存在";
            return ResponseResult.error(500, msg);
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
                return ResponseResult.success(code, msg);

            } else {
                msg = "注册失败";
                return ResponseResult.error(500, msg);
            }
        }
    }


    /**
     * 用户登录
     * @param username,password
     * @return
     */
    @Override
    public ResponseResult login(String username, String password) {
        // AuthenticationManager authenticate进行用户认证
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 如果认证没通过
        if (Objects.isNull(authenticate)) {
            return ResponseResult.error(500, "用户名或密码错误");
        }
        //如果认证通过
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        System.out.println("loginUser=========" + loginUser);
        String userId = loginUser.getUser().getId().toString();
        User saveUserBean = findUserInfoByUserName(loginUser.getUsername());
        loginUser.setUserId(saveUserBean.getId());
        String jwt = JwtUtil.createJWT(JSONObject.toJSONString(loginUser));
        redisCache.set(Constants.REDIS_LOGIN_USER_PREFIX + userId,loginUser,JwtUtil.EXPIRE_TIME);
        User user = loginUser.getUser();
        if (user.getStatus() == 1) {
            return ResponseResult.error(500, "您的账号已被禁用，请联系管理员");
        }
        Map map = new HashMap();
        map.put("userInfo", user);
        map.put("token", jwt);
        map.put("ts", System.currentTimeMillis());
        return new ResponseResult().ok(map);
    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public ResponseResult logout() {
        // 获取securityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        Long id = loginUser.getUser().getId();
        // 删除redis中的值
        redisCache.deleteObject(Constants.REDIS_LOGIN_USER_PREFIX+id);

        return  ResponseResult.success("注销成功");
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
    public ResponseResult updateUser(User user) {

        int flag = userMapper.updateById(user);
        User user1 = userMapper.selectById(user.getId());
        if (flag>0){
            Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
                    LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
                    User newUser = userMapper.selectById(user.getId());
                    loginUser.setUser(newUser);
            redisCache.set(Constants.REDIS_LOGIN_USER_PREFIX + user.getId(),loginUser,JwtUtil.EXPIRE_TIME);
            Map map = new HashMap();
            map.put("userInfo", user1);
            map.put("ts", System.currentTimeMillis());
            return ResponseResult.success("修改成功",map);
        }else{
            return ResponseResult.error(500,"修改失败");
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

    @Override
    public int modifyPassword(User user, String oldPassword, String newPassword) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", user.getId());
        updateWrapper.set("password", passwordEncoder.encode(newPassword));
        return userMapper.update(user, updateWrapper);
    }

}
