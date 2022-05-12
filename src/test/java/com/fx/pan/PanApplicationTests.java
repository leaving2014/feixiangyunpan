package com.fx.pan;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fx.pan.domain.FileBean;
import com.fx.pan.mapper.FileMapper;
import com.fx.pan.utils.DateUtil;
import com.fx.pan.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class PanApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(org.apache.commons.io.FileUtils.byteCountToDisplaySize(12233L)); //11 KB  Apache commons.io
        System.out.println(FileUtils.fileSizeUnitConversionAndUnit(12233L));//11.95 KB
    }



}
