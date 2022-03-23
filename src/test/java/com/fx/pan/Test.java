package com.fx.pan;


import com.fx.pan.utils.DateUtil;
import com.fx.pan.utils.FileUtil;
import org.apache.commons.io.FileUtils;

/**
 * @Author leaving
 * @Date 2021/11/25 19:26
 * @Version 1.0
 */

public class Test {




    @org.junit.jupiter.api.Test
    public void  test1() {
        String size = FileUtil.fileSizeUnitConversionAndUnit(2278153313L);
        System.out.println(size);
    }

    public static void main(String[] args) {
        System.out.println(FileUtils.byteCountToDisplaySize(12233L)); //11 KB  Apache commons.io
        System.out.println(FileUtil.fileSizeUnitConversionAndUnit(12233L));//11.95 KB
        System.out.println(DateUtil.getCurrentTime());
    }
}
