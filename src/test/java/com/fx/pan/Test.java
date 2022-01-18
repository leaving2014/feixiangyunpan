package com.fx.pan;

/**
 * @Author leaving
 * @Date 2021/11/25 19:26
 * @Version 1.0
 */

public class Test {

    /**
     *
     * @param rawSize
     * @return
     */
    public double fileSizeUnitConversion(long rawSize){
        //26.71KB    fileSize:27352.0
        double conversionSize=0;
        if (rawSize<1024){
            // B
            conversionSize= rawSize;
        }else if (rawSize<1024*1024){
            //KB
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize / 1024));
        }else if(rawSize<Math.pow(1024,3)){
            // MB
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize / Math.pow(1024,2)));
        }else if (rawSize<Math.pow(1024,4)){
            //GB
            conversionSize= Double.parseDouble(String.format("%.2f",rawSize / Math.pow(1024,3)));
        }
        return conversionSize;
    }

    /**
     *
     * @param rawSize
     * @return
     */
    public String fileSizeUnitConversionAndUnit(long rawSize){
        //26.71KB    fileSize:27352.0
        double conversionSize=0;
        String unit="";
        if (rawSize<1024){
            unit="B";
            conversionSize= rawSize;
        }else if (rawSize<1024*1024){
            unit="KB";
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize / 1024));
        }else if(rawSize<Math.pow(1024,3)){
            unit="MB";
            conversionSize=Double.parseDouble(String.format("%.2f",rawSize / Math.pow(1024,2)));
        }else if (rawSize<Math.pow(1024,4)){
            unit="GB";
            conversionSize= Double.parseDouble(String.format("%.2f",rawSize / Math.pow(1024,3)));
        }
        return conversionSize+" "+unit;
    }


    @org.junit.jupiter.api.Test
    public void  test1(){
        String size = fileSizeUnitConversionAndUnit(2278153313L);
        System.out.println(size);
    }
}
