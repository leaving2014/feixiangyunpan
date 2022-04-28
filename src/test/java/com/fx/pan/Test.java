package com.fx.pan;


import com.fx.pan.utils.DateUtil;
import com.fx.pan.utils.FileUtils;

/**
 * @Author leaving
 * @Date 2021/11/25 19:26
 * @Version 1.0
 */

public class Test {




    @org.junit.jupiter.api.Test
    public void  test1() {
        String size = FileUtils.fileSizeUnitConversionAndUnit(2278153313L);
        System.out.println(size);
    }

    public static void main(String[] args) {
        System.out.println(org.apache.commons.io.FileUtils.byteCountToDisplaySize(12233L)); //11 KB  Apache commons.io
        System.out.println(FileUtils.fileSizeUnitConversionAndUnit(12233L));//11.95 KB
        System.out.println(DateUtil.getCurrentTime());
    }
}
