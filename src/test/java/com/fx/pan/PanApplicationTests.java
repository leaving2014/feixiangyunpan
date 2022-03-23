package com.fx.pan;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fx.pan.domain.FileBean;
import com.fx.pan.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class PanApplicationTests {


    @Resource
    private FileMapper fileMapper;


    @Test
    void contextLoads() {

        // Update update = new LambdaUpdateChainWrapper(fileMapper);
        // update.setSql(true, "update file set parent_path_id = -1 where file_path = '/'");
        UpdateWrapper<FileBean> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("file_path", "/").set("parent_path_id", -1);
        fileMapper.update(null, updateWrapper);



    }



}
